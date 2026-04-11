package com.workflow.application.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflow.application.exception.BusinessException;
import com.workflow.application.exception.ResourceNotFoundException;
import com.workflow.application.usecase.ProcessVersionUseCase;
import com.workflow.application.usecase.command.CreateProcessVersionCommand;
import com.workflow.application.usecase.result.ProcessDeployResult;
import com.workflow.application.usecase.result.ProcessVersionResult;
import com.workflow.domain.entity.ProcessDefinition;
import com.workflow.domain.entity.ProcessVersion;
import com.workflow.domain.enums.DeployStatus;
import com.workflow.domain.repository.ProcessDefinitionRepository;
import com.workflow.domain.repository.ProcessVersionRepository;
import com.workflow.infrastructure.camunda.CamundaDeployService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessVersionService implements ProcessVersionUseCase {

    private final ProcessVersionRepository repository;
    private final ProcessDefinitionRepository processDefinitionRepository;
    private final CamundaDeployService camundaDeployService;

    @Override
    @Transactional
    public ProcessVersionResult createVersion(CreateProcessVersionCommand command) {
        ProcessDefinition definition = getProcessDefinition(command.processKey());
        Map<String, Object> data = command.data();
        int nextVersion = repository.findByProcessDefinitionIdOrderByVersionDesc(definition.getId()).stream()
                .map(ProcessVersion::getVersion)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        ProcessVersion processVersion = new ProcessVersion();
        processVersion.setProcessDefinition(definition);
        processVersion.setVersion(nextVersion);
        processVersion.setName(resolveName(definition, data, nextVersion));
        processVersion.setBpmnXml(requireText(readString(data, "bpmnXml"), "bpmnXml is required"));
        processVersion.setDescription(firstNonBlank(command.comment(), readString(data, "description")));
        processVersion.setCreatedBy(readString(data, "createdBy"));
        processVersion.setStatus(DeployStatus.DRAFT);

        ProcessVersion savedVersion = repository.save(processVersion);
        log.info("Created process version {} for processKey={}", savedVersion.getVersion(), command.processKey());
        return toResult(savedVersion);
    }

    @Override
    @Transactional
    public ProcessDeployResult deployVersion(Long id) {
        ProcessVersion processVersion = getProcessVersion(id);
        ProcessDefinition definition = processVersion.getProcessDefinition();

        if (isBlank(processVersion.getBpmnXml())) {
            throw new BusinessException("BPMN XML is required before deployment");
        }

        List<ProcessVersion> activeVersions = repository.findByProcessDefinitionIdAndStatusOrderByVersionDesc(
                definition.getId(),
                DeployStatus.DEPLOYED);
        activeVersions.stream()
                .filter(activeVersion -> !Objects.equals(activeVersion.getId(), processVersion.getId()))
                .forEach(activeVersion -> activeVersion.setStatus(DeployStatus.DEPRECATED));

        CamundaDeployService.DeploymentResult deploymentResult = camundaDeployService.deployBpmn(
                definition.getProcessKey(),
                processVersion.getVersion(),
                processVersion.getBpmnXml());

        processVersion.setStatus(DeployStatus.DEPLOYED);
        processVersion.setDeploymentId(deploymentResult.getDeploymentKey());
        processVersion.setCamundaDefinitionKey(String.valueOf(deploymentResult.getProcessDefinitionKey()));
        processVersion.setCamundaVersion(parseInteger(deploymentResult.getCamundaVersion(), "camundaVersion"));
        processVersion.setDeployedAt(LocalDateTime.now());

        repository.saveAll(activeVersions);
        ProcessVersion savedVersion = repository.save(processVersion);

        log.info("Deployed processKey={} version={} with deploymentId={}",
                definition.getProcessKey(),
                savedVersion.getVersion(),
                savedVersion.getDeploymentId());

        return ProcessDeployResult.builder()
                .processVersionId(savedVersion.getId())
                .processKey(definition.getProcessKey())
                .version(savedVersion.getVersion())
                .deploymentId(savedVersion.getDeploymentId())
                .camundaProcessDefinitionKey(parseLong(savedVersion.getCamundaDefinitionKey()))
                .camundaVersion(savedVersion.getCamundaVersion() == null
                        ? null
                        : String.valueOf(savedVersion.getCamundaVersion()))
                .message("Deployment completed successfully")
                .build();
    }

    @Override
    @Transactional
    public ProcessDeployResult rollback(String processKey, Integer targetVersion) {
        ProcessDefinition definition = getProcessDefinition(processKey);
        ProcessVersion processVersion = repository.findByProcessDefinitionIdAndVersion(definition.getId(), targetVersion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Process version not found for processKey=%s version=%s".formatted(processKey, targetVersion)));

        ProcessDeployResult deployResult = deployVersion(processVersion.getId());
        return ProcessDeployResult.builder()
                .processVersionId(deployResult.processVersionId())
                .processKey(deployResult.processKey())
                .version(deployResult.version())
                .deploymentId(deployResult.deploymentId())
                .camundaProcessDefinitionKey(deployResult.camundaProcessDefinitionKey())
                .camundaVersion(deployResult.camundaVersion())
                .message("Rollback completed successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessVersionResult> getVersionsByProcessKey(String processKey) {
        ProcessDefinition definition = getProcessDefinition(processKey);
        return repository.findByProcessDefinitionIdOrderByVersionDesc(definition.getId()).stream()
                .map(this::toResult)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessVersionResult getActiveVersion(String processKey) {
        ProcessDefinition definition = getProcessDefinition(processKey);
        ProcessVersion processVersion = repository.findByProcessDefinitionIdAndStatusOrderByVersionDesc(
                definition.getId(),
                DeployStatus.DEPLOYED).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active process version not found for processKey=" + processKey));
        return toResult(processVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessVersionResult> getAllVersions() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(
                        (ProcessVersion version) -> version.getProcessDefinition().getProcessKey(),
                        Comparator.nullsLast(String::compareTo))
                        .thenComparing(ProcessVersion::getVersion, Comparator.reverseOrder()))
                .map(this::toResult)
                .toList();
    }

    @Override
    @Transactional
    public ProcessVersionResult updateBpmnXml(Long id, String bpmnXml) {
        ProcessVersion processVersion = getProcessVersion(id);
        if (processVersion.getStatus() == DeployStatus.DEPLOYED) {
            throw new BusinessException("Cannot update BPMN XML of a deployed version");
        }

        processVersion.setBpmnXml(requireText(bpmnXml, "bpmnXml is required"));
        ProcessVersion savedVersion = repository.save(processVersion);
        return toResult(savedVersion);
    }

    @Override
    @Transactional
    public void deleteVersion(Long id) {
        ProcessVersion processVersion = getProcessVersion(id);
        if (processVersion.getStatus() == DeployStatus.DEPLOYED) {
            throw new BusinessException("Cannot delete a deployed version");
        }

        repository.delete(processVersion);
        log.info("Deleted process version id={} processKey={} version={}",
                processVersion.getId(),
                processVersion.getProcessDefinition().getProcessKey(),
                processVersion.getVersion());
    }

    private ProcessDefinition getProcessDefinition(String processKey) {
        ProcessDefinition definition = processDefinitionRepository.findByProcessKey(processKey);
        if (definition == null) {
            throw new ResourceNotFoundException("Process definition not found for processKey=" + processKey);
        }
        return definition;
    }

    private ProcessVersion getProcessVersion(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Process version not found with id=" + id));
    }

    private ProcessVersionResult toResult(ProcessVersion processVersion) {
        return ProcessVersionResult.builder()
                .id(processVersion.getId())
                .processKey(processVersion.getProcessDefinition().getProcessKey())
                .version(processVersion.getVersion())
                .name(processVersion.getName())
                .description(processVersion.getDescription())
                .status(processVersion.getStatus())
                .camundaProcessDefinitionKey(parseLong(processVersion.getCamundaDefinitionKey()))
                .camundaVersion(processVersion.getCamundaVersion() == null
                        ? null
                        : String.valueOf(processVersion.getCamundaVersion()))
                .deploymentId(processVersion.getDeploymentId())
                .createdBy(processVersion.getCreatedBy())
                .createdAt(processVersion.getCreatedAt())
                .deployedAt(processVersion.getDeployedAt())
                .build();
    }

    private String resolveName(ProcessDefinition definition, Map<String, Object> data, int version) {
        String name = readString(data, "name");
        if (!isBlank(name)) {
            return name;
        }

        if (!isBlank(definition.getName())) {
            return definition.getName() + " v" + version;
        }

        return definition.getProcessKey() + " v" + version;
    }

    private String readString(Map<String, Object> data, String key) {
        if (data == null || !data.containsKey(key) || data.get(key) == null) {
            return null;
        }
        return String.valueOf(data.get(key));
    }

    private String firstNonBlank(String first, String second) {
        return !isBlank(first) ? first : second;
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

    private Long parseLong(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException exception) {
            log.warn("Unable to parse camundaDefinitionKey={} to Long", value);
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
