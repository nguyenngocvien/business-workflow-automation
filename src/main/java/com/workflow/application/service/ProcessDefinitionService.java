package com.workflow.application.service;

import com.workflow.application.exception.BusinessException;
import com.workflow.application.exception.ResourceNotFoundException;
import com.workflow.application.usecase.WorkflowDefinitionUseCase;
import com.workflow.application.usecase.command.DeployWorkflowBpmnCommand;
import com.workflow.application.usecase.command.DeployWorkflowDefinitionCommand;
import com.workflow.application.usecase.command.StepDefinitionCommand;
import com.workflow.application.usecase.command.UploadWorkflowBpmnCommand;
import com.workflow.application.usecase.result.DeploymentResult;
import com.workflow.application.usecase.result.WorkflowBpmnResult;
import com.workflow.application.usecase.result.WorkflowDefinitionDeploymentResult;
import com.workflow.application.usecase.result.WorkflowDefinitionResult;
import com.workflow.application.usecase.result.WorkflowStepDefinitionResult;
import com.workflow.domain.entity.ProcessDefinition;
import com.workflow.domain.entity.ProcessDeployment;
import com.workflow.domain.entity.ProcessStepDefinition;
import com.workflow.domain.entity.ProcessVersion;
import com.workflow.domain.enums.DeployStatus;
import com.workflow.domain.repository.ProcessDeploymentRepository;
import com.workflow.domain.repository.ProcessDefinitionRepository;
import com.workflow.domain.repository.ProcessStepDefinitionRepository;
import com.workflow.domain.repository.ProcessVersionRepository;
import com.workflow.infrastructure.camunda.CamundaDeployService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessDefinitionService implements WorkflowDefinitionUseCase {

    private final ProcessDefinitionRepository processDefinitionRepository;
    private final ProcessStepDefinitionRepository processStepDefinitionRepository;
    private final ProcessVersionRepository processVersionRepository;
    private final ProcessDeploymentRepository processDeploymentRepository;
    private final CamundaDeployService camundaDeployService;

    @Override
    public WorkflowDefinitionResult deployDefinition(DeployWorkflowDefinitionCommand command) {
        validateDeployDefinitionCommand(command);
        ProcessDefinition definition = processDefinitionRepository.findByProcessKey(command.workflowKey());
        boolean isNew = definition == null;
        if (isNew) {
            definition = new ProcessDefinition();
            definition.setCreatedAt(LocalDateTime.now());
            definition.setCreatedBy(command.createdBy());
            definition.setIsActive(true);
            definition.setProcessKey(command.workflowKey());
        }

        definition.setApplicationName(command.applicationName());
        definition.setName(command.workflowName());
        definition.setDescription(command.description());
        definition.setUpdatedAt(LocalDateTime.now());
        definition.setIsActive(true);
        ProcessDefinition savedDefinition = processDefinitionRepository.save(definition);

        replaceSteps(savedDefinition, command.steps());

        return toResult(savedDefinition);
    }

    @Override
    public WorkflowBpmnResult uploadBpmn(UploadWorkflowBpmnCommand command) {
        validateUploadBpmnCommand(command);
        ProcessDefinition definition = getDefinitionEntity(command.definitionId());
        int nextVersion = getNextVersion(definition.getId());

        ProcessVersion version = new ProcessVersion();
        version.setProcessDefinition(definition);
        version.setVersion(nextVersion);
        version.setName(definition.getName() + " v" + nextVersion);
        version.setDescription("Uploaded BPMN");
        version.setBpmnXml(requireText(command.bpmnXml(), "bpmnXml is required"));
        version.setCreatedBy(command.uploadedBy());
        version.setStatus(DeployStatus.DRAFT);

        ProcessVersion savedVersion = processVersionRepository.save(version);
        return WorkflowBpmnResult.builder()
                .id(savedVersion.getId())
                .workflowDefinitionId(definition.getId())
                .workflowKey(definition.getProcessKey())
                .definitionVersion(savedVersion.getVersion())
                .resourceName(command.resourceName())
                .deploymentStatus(savedVersion.getStatus().name())
                .bpmnChecksum(checksum(command.bpmnXml()))
                .createdAt(savedVersion.getCreatedAt())
                .createdBy(savedVersion.getCreatedBy())
                .updatedAt(savedVersion.getUpdatedAt())
                .updatedBy(null)
                .deployedAt(savedVersion.getDeployedAt())
                .deployedBy(null)
                .build();
    }

    @Override
    public WorkflowDefinitionDeploymentResult deployBpmn(DeployWorkflowBpmnCommand command) {
        if (command == null || command.definitionId() == null) {
            throw new BusinessException("definitionId is required");
        }
        if (isBlank(command.deployedBy())) {
            throw new BusinessException("deployedBy is required");
        }

        ProcessDefinition definition = getDefinitionEntity(command.definitionId());
        ProcessVersion processVersion = processVersionRepository.findByProcessDefinitionIdOrderByVersionDesc(definition.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Process version not found for definitionId=" + command.definitionId()));

        if (isBlank(processVersion.getBpmnXml())) {
            throw new BusinessException("BPMN XML is required before deployment");
        }

        List<ProcessVersion> activeVersions = processVersionRepository.findByProcessDefinitionIdAndStatusOrderByVersionDesc(
                definition.getId(),
                DeployStatus.DEPLOYED);
        activeVersions.stream()
                .filter(activeVersion -> !Objects.equals(activeVersion.getId(), processVersion.getId()))
                .forEach(activeVersion -> activeVersion.setStatus(DeployStatus.DEPRECATED));

        DeploymentResult deploymentResult = camundaDeployService.deployBpmn(
                definition.getProcessKey(),
                processVersion.getVersion(),
                processVersion.getBpmnXml());

        processVersion.setStatus(DeployStatus.DEPLOYED);
        processVersion.setDeploymentId(deploymentResult.getDeploymentKey());
        processVersion.setCamundaDefinitionKey(String.valueOf(deploymentResult.getProcessDefinitionKey()));
        processVersion.setCamundaVersion(parseInteger(deploymentResult.getCamundaVersion(), "camundaVersion"));
        processVersion.setDeployedAt(LocalDateTime.now());

        processVersionRepository.saveAll(activeVersions);
        ProcessVersion savedVersion = processVersionRepository.save(processVersion);

        ProcessDeployment deployment = new ProcessDeployment();
        deployment.setProcessDefinition(definition);
        deployment.setProcessVersion(savedVersion);
        deployment.setDeploymentId(savedVersion.getDeploymentId());
        deployment.setDeployedBy(command.deployedBy());
        deployment.setDeployedAt(savedVersion.getDeployedAt());
        deployment.setEnvironment("default");
        ProcessDeployment savedDeployment = processDeploymentRepository.save(deployment);

        return WorkflowDefinitionDeploymentResult.builder()
                .id(savedDeployment.getId())
                .workflowDefinitionId(definition.getId())
                .workflowKey(definition.getProcessKey())
                .definitionVersion(savedVersion.getVersion())
                .deploymentStatus(savedVersion.getStatus().name())
                .camundaDeploymentId(savedVersion.getDeploymentId())
                .camundaProcessDefinitionId(savedVersion.getCamundaDefinitionKey())
                .camundaProcessDefinitionKey(definition.getProcessKey())
                .camundaProcessDefinitionVersion(savedVersion.getCamundaVersion())
                .createdAt(savedDeployment.getDeployedAt())
                .deployedAt(savedVersion.getDeployedAt())
                .deployedBy(savedDeployment.getDeployedBy())
                .failureReason(null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDefinitionDeploymentResult> getDeploymentHistory(Long definitionId) {
        ProcessDefinition definition = getDefinitionEntity(definitionId);
        return processDeploymentRepository.findAll().stream()
                .filter(deployment -> deployment.getProcessDefinition() != null
                        && Objects.equals(deployment.getProcessDefinition().getId(), definition.getId()))
                .sorted(Comparator.comparing(ProcessDeployment::getDeployedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toDeploymentResult)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowDefinitionResult getDefinition(Long definitionId) {
        ProcessDefinition definition = getDefinitionEntity(definitionId);
        return toResult(definition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDefinitionResult> getDefinitionsByWorkflowKey(String workflowKey) {
        if (isBlank(workflowKey)) {
            throw new BusinessException("workflowKey is required");
        }
        ProcessDefinition definition = processDefinitionRepository.findByProcessKey(workflowKey);
        if (definition == null) {
            return List.of();
        }
        return List.of(toResult(definition));
    }

    private void replaceSteps(ProcessDefinition definition, List<StepDefinitionCommand> steps) {
        List<ProcessStepDefinition> existingSteps = processStepDefinitionRepository.findAll().stream()
                .filter(step -> step.getProcessDefinition() != null
                        && Objects.equals(step.getProcessDefinition().getId(), definition.getId()))
                .toList();
        if (!existingSteps.isEmpty()) {
            processStepDefinitionRepository.deleteAll(existingSteps);
        }

        if (steps == null || steps.isEmpty()) {
            return;
        }

        List<ProcessStepDefinition> newSteps = steps.stream()
                .map(step -> toEntity(definition, step))
                .toList();
        processStepDefinitionRepository.saveAll(newSteps);
    }

    private ProcessStepDefinition toEntity(ProcessDefinition definition, StepDefinitionCommand command) {
        ProcessStepDefinition step = new ProcessStepDefinition();
        step.setProcessDefinition(definition);
        step.setStepName(command.stepName());
        step.setStepCode(command.stepCode());
        step.setStepType(command.stepType());
        step.setStepOrder(command.stepOrder());
        step.setNextStepCode(command.nextStepCode());
        step.setConditionExpression(command.conditionExpression());
        step.setSlaMinutes(command.slaMinutes());
        step.setCreatedAt(LocalDateTime.now());
        return step;
    }

    private WorkflowDefinitionResult toResult(ProcessDefinition definition) {
        Integer latestVersion = processVersionRepository.findByProcessDefinitionIdOrderByVersionDesc(definition.getId())
                .stream()
                .map(ProcessVersion::getVersion)
                .findFirst()
                .orElse(1);

        List<WorkflowStepDefinitionResult> steps = processStepDefinitionRepository.findAll().stream()
                .filter(step -> step.getProcessDefinition() != null
                        && Objects.equals(step.getProcessDefinition().getId(), definition.getId()))
                .sorted(Comparator.comparing(ProcessStepDefinition::getStepOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ProcessStepDefinition::getStepCode, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toResult)
                .toList();

        return WorkflowDefinitionResult.builder()
                .id(definition.getId())
                .applicationName(definition.getApplicationName())
                .workflowName(definition.getName())
                .workflowKey(definition.getProcessKey())
                .version(latestVersion)
                .description(definition.getDescription())
                .active(definition.getIsActive())
                .createdBy(definition.getCreatedBy())
                .createdAt(definition.getCreatedAt())
                .updatedAt(definition.getUpdatedAt())
                .steps(steps)
                .build();
    }

    private WorkflowStepDefinitionResult toResult(ProcessStepDefinition step) {
        return WorkflowStepDefinitionResult.builder()
                .id(step.getId())
                .stepName(step.getStepName())
                .stepCode(step.getStepCode())
                .stepType(step.getStepType())
                .stepOrder(step.getStepOrder())
                .nextStepCode(step.getNextStepCode())
                .conditionExpression(step.getConditionExpression())
                .slaMinutes(step.getSlaMinutes())
                .createdAt(step.getCreatedAt())
                .build();
    }

    private WorkflowDefinitionDeploymentResult toDeploymentResult(ProcessDeployment deployment) {
        ProcessDefinition definition = deployment.getProcessDefinition();
        ProcessVersion version = deployment.getProcessVersion();
        return WorkflowDefinitionDeploymentResult.builder()
                .id(deployment.getId())
                .workflowDefinitionId(definition == null ? null : definition.getId())
                .workflowKey(definition == null ? null : definition.getProcessKey())
                .definitionVersion(version == null ? null : version.getVersion())
                .deploymentStatus(version == null || version.getStatus() == null ? null : version.getStatus().name())
                .camundaDeploymentId(deployment.getDeploymentId())
                .camundaProcessDefinitionId(version == null ? null : version.getCamundaDefinitionKey())
                .camundaProcessDefinitionKey(definition == null ? null : definition.getProcessKey())
                .camundaProcessDefinitionVersion(version == null ? null : version.getCamundaVersion())
                .createdAt(deployment.getDeployedAt())
                .deployedAt(deployment.getDeployedAt())
                .deployedBy(deployment.getDeployedBy())
                .failureReason(null)
                .build();
    }

    private ProcessDefinition getDefinitionEntity(Long definitionId) {
        if (definitionId == null) {
            throw new BusinessException("definitionId is required");
        }
        return processDefinitionRepository.findById(definitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow definition not found with id=" + definitionId));
    }

    private int getNextVersion(Long definitionId) {
        return processVersionRepository.findByProcessDefinitionIdOrderByVersionDesc(definitionId).stream()
                .map(ProcessVersion::getVersion)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void validateDeployDefinitionCommand(DeployWorkflowDefinitionCommand command) {
        if (command == null) {
            throw new BusinessException("Deploy definition command is required");
        }
        if (isBlank(command.applicationName())) {
            throw new BusinessException("applicationName is required");
        }
        if (isBlank(command.workflowName())) {
            throw new BusinessException("workflowName is required");
        }
        if (isBlank(command.workflowKey())) {
            throw new BusinessException("workflowKey is required");
        }
        if (command.steps() == null || command.steps().isEmpty()) {
            throw new BusinessException("steps are required");
        }
    }

    private void validateUploadBpmnCommand(UploadWorkflowBpmnCommand command) {
        if (command == null) {
            throw new BusinessException("Upload BPMN command is required");
        }
        if (command.definitionId() == null) {
            throw new BusinessException("definitionId is required");
        }
        if (isBlank(command.bpmnXml())) {
            throw new BusinessException("bpmnXml is required");
        }
    }

    private String requireText(String value, String message) {
        if (isBlank(value)) {
            throw new BusinessException(message);
        }
        return value;
    }

    private Integer parseInteger(String value, String fieldName) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new BusinessException("Invalid %s value: %s".formatted(fieldName, value));
        }
    }

    private String checksum(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new BusinessException("Unable to calculate BPMN checksum");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public boolean hasFirstStep(String processInstanceId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasFirstStep'");
    }
}
