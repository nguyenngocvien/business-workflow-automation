package com.workflow.application.service;

import com.workflow.application.exception.BusinessException;
import com.workflow.application.exception.ResourceNotFoundException;
import com.workflow.application.usecase.ProcessInstanceUseCase;
import com.workflow.application.usecase.command.StartProcessCommand;
import com.workflow.application.usecase.result.ProcessInstanceResult;
import com.workflow.application.usecase.result.WorkflowLookupItemResult;
import com.workflow.application.usecase.result.WorkflowProgressResult;
import com.workflow.application.usecase.result.WorkflowProgressStepResult;
import com.workflow.application.usecase.result.WorkflowSearchOptionsResult;
import com.workflow.application.usecase.result.WorkflowSearchResult;
import com.workflow.application.usecase.result.WorkflowStepInstanceResult;
import com.workflow.application.usecase.result.WorkflowTaskResult;
import com.workflow.domain.entity.ProcessDefinition;
import com.workflow.domain.entity.ProcessInstance;
import com.workflow.domain.entity.ProcessStepDefinition;
import com.workflow.domain.entity.UserTask;
import com.workflow.domain.entity.ProcessVersion;
import com.workflow.domain.enums.DeployStatus;
import com.workflow.domain.enums.ProcessStatus;
import com.workflow.domain.repository.ProcessDefinitionRepository;
import com.workflow.domain.repository.ProcessInstanceRepository;
import com.workflow.domain.repository.ProcessStepDefinitionRepository;
import com.workflow.domain.repository.ProcessTaskRepository;
import com.workflow.domain.repository.ProcessVersionRepository;
import com.workflow.infrastructure.camunda.CamundaProcessService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcessInstanceService implements ProcessInstanceUseCase {

    private final ProcessDefinitionRepository processDefinitionRepository;
    private final ProcessVersionRepository processVersionRepository;
    private final ProcessStepDefinitionRepository processStepDefinitionRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final ProcessTaskRepository processTaskRepository;
    private final CamundaProcessService camundaProcessService;

    public ProcessInstanceService(
            ProcessDefinitionRepository processDefinitionRepository,
            ProcessVersionRepository processVersionRepository,
            ProcessStepDefinitionRepository processStepDefinitionRepository,
            ProcessInstanceRepository processInstanceRepository,
            ProcessTaskRepository processTaskRepository,
            CamundaProcessService camundaProcessService) {
        this.processDefinitionRepository = processDefinitionRepository;
        this.processVersionRepository = processVersionRepository;
        this.processStepDefinitionRepository = processStepDefinitionRepository;
        this.processInstanceRepository = processInstanceRepository;
        this.processTaskRepository = processTaskRepository;
        this.camundaProcessService = camundaProcessService;
    }

    @Override
    public ProcessInstanceResult startProcess(StartProcessCommand command) {
        if (command == null) {
            throw new BusinessException("Start process command is required");
        }
        if (isBlank(command.workflowKey())) {
            throw new BusinessException("workflowKey is required");
        }

        ProcessDefinition definition = getProcessDefinition(command.workflowKey());
        ProcessVersion activeVersion = getActiveVersion(definition);
        String currentStepCode = resolveFirstStepCode(definition);
        long camundaInstanceKey = camundaProcessService.startProcess(
                definition.getProcessKey(),
                command.formData() == null ? Map.of() : command.formData());

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setInstanceId(String.valueOf(camundaInstanceKey));
        processInstance.setProcessDefinition(definition);
        processInstance.setBusinessKey(command.businessKey());
        processInstance.setStatus(ProcessStatus.RUNNING);
        processInstance.setCurrentStepCode(currentStepCode);
        processInstance.setStartedBy(command.startedBy());
        processInstance.setStartedAt(LocalDateTime.now());

        ProcessInstance savedInstance = processInstanceRepository.save(processInstance);
        return toResult(savedInstance, activeVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessInstanceResult getProcessInstance(Long workflowInstanceId) {
        ProcessInstance processInstance = getProcessInstanceEntity(workflowInstanceId);
        ProcessVersion activeVersion = getActiveVersion(processInstance.getProcessDefinition());
        return toResult(processInstance, activeVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowProgressResult getProgress(Long workflowInstanceId) {
        ProcessInstance processInstance = getProcessInstanceEntity(workflowInstanceId);
        ProcessDefinition definition = processInstance.getProcessDefinition();
        List<ProcessStepDefinition> steps = getStepDefinitions(definition);
        Integer currentOrder = getCurrentStepOrder(steps, processInstance.getCurrentStepCode());

        return WorkflowProgressResult.builder()
                .workflowInstanceId(processInstance.getId())
                .businessKey(processInstance.getBusinessKey())
                .workflowStatus(processInstance.getStatus() == null ? null : processInstance.getStatus().name())
                .currentStepCode(processInstance.getCurrentStepCode())
                .totalSteps(steps.size())
                .steps(steps.stream()
                        .map(step -> toProgressStepResult(processInstance, step, currentOrder))
                        .toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowSearchOptionsResult getProcessSearchOptions(String workflowKey) {
        List<ProcessDefinition> definitions = processDefinitionRepository.findAll().stream()
                .filter(definition -> isBlank(workflowKey) || workflowKey.equalsIgnoreCase(definition.getProcessKey()))
                .toList();

        List<WorkflowLookupItemResult> workflows = definitions.stream()
                .sorted(Comparator.comparing(ProcessDefinition::getProcessKey, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(definition -> WorkflowLookupItemResult.builder()
                        .code(definition.getProcessKey())
                        .name(definition.getName())
                        .build())
                .toList();

        List<WorkflowLookupItemResult> steps = definitions.stream()
                .flatMap(definition -> getStepDefinitions(definition).stream())
                .sorted(Comparator.comparing(ProcessStepDefinition::getStepOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ProcessStepDefinition::getStepCode, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(step -> WorkflowLookupItemResult.builder()
                        .code(step.getStepCode())
                        .name(step.getStepName())
                        .build())
                .toList();

        List<String> statuses = java.util.Arrays.stream(ProcessStatus.values())
                .map(Enum::name)
                .toList();

        return WorkflowSearchOptionsResult.builder()
                .workflows(workflows)
                .steps(steps)
                .statuses(statuses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowSearchResult> searchProcesses(
            String applicationName,
            String workflowKey,
            String currentStepCode,
            String status,
            String businessKey,
            String assignee) {
        return processInstanceRepository.findAll().stream()
                .filter(instance -> matches(instance.getProcessDefinition(), applicationName, workflowKey))
                .filter(instance -> matches(instance.getCurrentStepCode(), currentStepCode))
                .filter(instance -> matches(instance.getBusinessKey(), businessKey))
                .filter(instance -> matchesStatus(instance.getStatus(), status))
                .filter(instance -> matchesAssignee(instance, assignee))
                .map(this::toSearchResult)
                .toList();
    }

    private ProcessDefinition getProcessDefinition(String workflowKey) {
        ProcessDefinition definition = processDefinitionRepository.findByProcessKey(workflowKey);
        if (definition == null) {
            throw new ResourceNotFoundException("Process definition not found for workflowKey=" + workflowKey);
        }
        return definition;
    }

    private ProcessVersion getActiveVersion(ProcessDefinition definition) {
        return processVersionRepository.findByProcessDefinitionIdAndStatusOrderByVersionDesc(definition.getId(), DeployStatus.DEPLOYED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active process version not found for workflowKey=" + definition.getProcessKey()));
    }

    private ProcessInstance getProcessInstanceEntity(Long workflowInstanceId) {
        if (workflowInstanceId == null) {
            throw new BusinessException("workflowInstanceId is required");
        }
        return processInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Process instance not found with id=" + workflowInstanceId));
    }

    private List<ProcessStepDefinition> getStepDefinitions(ProcessDefinition definition) {
        return processStepDefinitionRepository.findAll().stream()
                .filter(step -> step.getProcessDefinition() != null
                        && Objects.equals(step.getProcessDefinition().getId(), definition.getId()))
                .sorted(Comparator.comparing(ProcessStepDefinition::getStepOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ProcessStepDefinition::getStepCode, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private String resolveFirstStepCode(ProcessDefinition definition) {
        return getStepDefinitions(definition).stream()
                .map(ProcessStepDefinition::getStepCode)
                .findFirst()
                .orElse(null);
    }

    private Integer getCurrentStepOrder(List<ProcessStepDefinition> steps, String currentStepCode) {
        if (isBlank(currentStepCode)) {
            return null;
        }
        return steps.stream()
                .filter(step -> currentStepCode.equalsIgnoreCase(step.getStepCode()))
                .map(ProcessStepDefinition::getStepOrder)
                .findFirst()
                .orElse(null);
    }

    private WorkflowProgressStepResult toProgressStepResult(
            ProcessInstance processInstance,
            ProcessStepDefinition step,
            Integer currentOrder) {
        Integer stepOrder = step.getStepOrder();
        String status = deriveStepStatus(stepOrder, currentOrder);
        UserTask task = getLatestTaskForStep(processInstance.getId(), step.getStepCode());

        return WorkflowProgressStepResult.builder()
                .stepOrder(stepOrder)
                .stepCode(step.getStepCode())
                .stepName(step.getStepName())
                .stepType(step.getStepType())
                .processor(task == null ? null : task.getAssignee())
                .processorFullName(task == null ? null : task.getAssignee())
                .status(status)
                .currentStep(step.getStepCode() != null
                        && processInstance.getCurrentStepCode() != null
                        && step.getStepCode().equalsIgnoreCase(processInstance.getCurrentStepCode()))
                .startedAt(task == null ? null : task.getCreatedAt())
                .endedAt(task == null ? null : task.getCompletedAt())
                .build();
    }

    private String deriveStepStatus(Integer stepOrder, Integer currentOrder) {
        if (stepOrder == null || currentOrder == null) {
            return "PENDING";
        }
        if (stepOrder < currentOrder) {
            return "COMPLETED";
        }
        if (stepOrder.equals(currentOrder)) {
            return "CURRENT";
        }
        return "PENDING";
    }

    private UserTask getLatestTaskForStep(Long processInstanceId, String stepCode) {
        return processTaskRepository.findByProcessInstance_IdOrderByCreatedAtDesc(processInstanceId).stream()
                .filter(task -> stepCode != null && stepCode.equalsIgnoreCase(task.getTaskCode()))
                .findFirst()
                .orElse(null);
    }

    private ProcessInstanceResult toResult(ProcessInstance processInstance, ProcessVersion activeVersion) {
        List<ProcessStepDefinition> steps = getStepDefinitions(processInstance.getProcessDefinition());
        List<UserTask> tasks = processTaskRepository.findByProcessInstance_IdOrderByCreatedAtDesc(processInstance.getId());

        return ProcessInstanceResult.builder()
                .id(processInstance.getId())
                .workflowDefinitionId(processInstance.getProcessDefinition() == null
                        ? null
                        : processInstance.getProcessDefinition().getId())
                .businessKey(processInstance.getBusinessKey())
                .status(processInstance.getStatus() == null ? null : processInstance.getStatus().name())
                .currentStepCode(processInstance.getCurrentStepCode())
                .startedBy(processInstance.getStartedBy())
                .startedAt(processInstance.getStartedAt())
                .endedAt(processInstance.getEndedAt())
                .stepInstances(buildStepInstances(steps, tasks, processInstance))
                .build();
    }

    private List<WorkflowStepInstanceResult> buildStepInstances(
            List<ProcessStepDefinition> steps,
            List<UserTask> tasks,
            ProcessInstance processInstance) {
        return steps.stream()
                .map(step -> {
                    List<WorkflowTaskResult> stepTasks = tasks.stream()
                            .filter(task -> step.getStepCode() != null && step.getStepCode().equalsIgnoreCase(task.getTaskCode()))
                            .map(this::toTaskResult)
                            .toList();
                    boolean currentStep = step.getStepCode() != null
                            && step.getStepCode().equalsIgnoreCase(processInstance.getCurrentStepCode());
                    String status = currentStep ? "CURRENT" : (stepTasks.isEmpty() ? "PENDING" : "COMPLETED");
                    return WorkflowStepInstanceResult.builder()
                            .id(step.getId())
                            .stepDefinitionId(step.getId())
                            .stepCode(step.getStepCode())
                            .status(status)
                            .processedBy(stepTasks.isEmpty() ? null : stepTasks.get(0).assignee())
                            .startedAt(stepTasks.isEmpty() ? null : stepTasks.get(0).createdAt())
                            .endedAt(stepTasks.isEmpty() ? null : stepTasks.get(0).completedAt())
                            .tasks(stepTasks)
                            .build();
                })
                .toList();
    }

    private WorkflowTaskResult toTaskResult(UserTask task) {
        return WorkflowTaskResult.builder()
                .id(task.getId())
                .workflowInstanceId(task.getProcessInstance() == null ? null : task.getProcessInstance().getId())
                .stepInstanceId(null)
                .taskName(task.getTaskName())
                .taskCode(task.getTaskCode())
                .assignee(task.getAssignee())
                .owner(task.getOwner())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .claimedAt(task.getClaimedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }

    private WorkflowSearchResult toSearchResult(ProcessInstance instance) {
        UserTask latestTask = processTaskRepository.findByProcessInstance_IdOrderByCreatedAtDesc(instance.getId())
                .stream()
                .findFirst()
                .orElse(null);

        return WorkflowSearchResult.builder()
                .wfInstanceId(instance.getId())
                .businessKey(instance.getBusinessKey())
                .workflowStatus(instance.getStatus() == null ? null : instance.getStatus().name())
                .currentStepCode(instance.getCurrentStepCode())
                .assignee(latestTask == null ? null : latestTask.getAssignee())
                .assigneeFullName(latestTask == null ? null : latestTask.getAssignee())
                .taskStatus(latestTask == null ? null : latestTask.getStatus())
                .reminderDate(latestTask == null ? null : latestTask.getDueDate())
                .taskCreatedAt(latestTask == null ? null : latestTask.getCreatedAt())
                .completedAt(latestTask == null ? null : latestTask.getCompletedAt())
                .build();
    }

    private boolean matches(ProcessDefinition definition, String applicationName, String workflowKey) {
        if (definition == null) {
            return false;
        }
        return matches(definition.getApplicationName(), applicationName)
                && matches(definition.getProcessKey(), workflowKey);
    }

    private boolean matches(String value, String filter) {
        return isBlank(filter) || (value != null && value.toLowerCase().contains(filter.toLowerCase()));
    }

    private boolean matchesStatus(ProcessStatus processStatus, String filter) {
        return isBlank(filter) || (processStatus != null && processStatus.name().equalsIgnoreCase(filter));
    }

    private boolean matchesAssignee(ProcessInstance instance, String assignee) {
        if (isBlank(assignee)) {
            return true;
        }
        return processTaskRepository.findByProcessInstance_IdOrderByCreatedAtDesc(instance.getId()).stream()
                .anyMatch(task -> task.getAssignee() != null && task.getAssignee().equalsIgnoreCase(assignee));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
