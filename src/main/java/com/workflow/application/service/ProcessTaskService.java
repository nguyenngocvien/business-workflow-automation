package com.workflow.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.application.exception.BusinessException;
import com.workflow.application.exception.ResourceNotFoundException;
import com.workflow.application.usecase.ProcessTaskUseCase;
import com.workflow.application.usecase.command.ClaimTaskByCandidateCommand;
import com.workflow.application.usecase.command.ClaimTaskCommand;
import com.workflow.application.usecase.command.CompleteTaskCommand;
import com.workflow.application.usecase.command.CreateWorkflowTaskIdentityLinkCommand;
import com.workflow.application.usecase.command.ReassignTaskCommand;
import com.workflow.application.usecase.command.SaveTaskDataCommand;
import com.workflow.application.usecase.result.ClaimableTaskResult;
import com.workflow.application.usecase.result.WorkflowTaskIdentityLinkResult;
import com.workflow.application.usecase.result.WorkflowTaskResult;
import com.workflow.domain.entity.ProcessData;
import com.workflow.domain.entity.ProcessHistory;
import com.workflow.domain.entity.ProcessInstance;
import com.workflow.domain.entity.ProcessTaskDataHistory;
import com.workflow.domain.entity.ProcessTaskIdentityLink;
import com.workflow.domain.entity.UserTask;
import com.workflow.domain.entity.UserTaskAssignmentHistory;
import com.workflow.domain.repository.ProcessHistoryRepository;
import com.workflow.domain.repository.ProcessTaskAssignmentHistoryRepository;
import com.workflow.domain.repository.ProcessTaskDataHistoryRepository;
import com.workflow.domain.repository.ProcessTaskDataRepository;
import com.workflow.domain.repository.ProcessTaskIdentityLinkRepository;
import com.workflow.domain.repository.ProcessTaskRepository;
import com.workflow.infrastructure.camunda.CamundaTaskService;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcessTaskService implements ProcessTaskUseCase {

    private static final Set<String> ALLOWED_LINK_TYPES = Set.of("CANDIDATE", "ASSIGNEE", "OWNER", "PARTICIPANT");

    private final ProcessTaskRepository processTaskRepository;
    private final ProcessTaskIdentityLinkRepository processTaskIdentityLinkRepository;
    private final ProcessTaskDataRepository processTaskDataRepository;
    private final ProcessTaskDataHistoryRepository processTaskDataHistoryRepository;
    private final ProcessTaskAssignmentHistoryRepository processTaskAssignmentHistoryRepository;
    private final ProcessHistoryRepository processHistoryRepository;
    private final CamundaTaskService camundaTaskService;
    private final ObjectMapper objectMapper;

    public ProcessTaskService(
            ProcessTaskRepository processTaskRepository,
            ProcessTaskIdentityLinkRepository processTaskIdentityLinkRepository,
            ProcessTaskDataRepository processTaskDataRepository,
            ProcessTaskDataHistoryRepository processTaskDataHistoryRepository,
            ProcessTaskAssignmentHistoryRepository processTaskAssignmentHistoryRepository,
            ProcessHistoryRepository processHistoryRepository,
            CamundaTaskService camundaTaskService,
            ObjectMapper objectMapper) {
        this.processTaskRepository = processTaskRepository;
        this.processTaskIdentityLinkRepository = processTaskIdentityLinkRepository;
        this.processTaskDataRepository = processTaskDataRepository;
        this.processTaskDataHistoryRepository = processTaskDataHistoryRepository;
        this.processTaskAssignmentHistoryRepository = processTaskAssignmentHistoryRepository;
        this.processHistoryRepository = processHistoryRepository;
        this.camundaTaskService = camundaTaskService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimableTaskResult> getClaimableTasks(String username) {
        if (isBlank(username)) {
            throw new BusinessException("username is required");
        }

        Set<String> pendingTaskIds = getPendingCamundaTaskIds();
        return processTaskRepository.findAll().stream()
                .filter(task -> pendingTaskIds.contains(task.getTaskId()))
                .filter(task -> isClaimableForUsername(task, username))
                .map(task -> toClaimableResult(task, username))
                .toList();
    }

    @Override
    public WorkflowTaskResult claimTask(ClaimTaskCommand command) {
        validateClaimCommand(command == null ? null : command.taskId(), command == null ? null : command.assignee(), command == null ? null : command.actionBy());
        UserTask task = getTask(command.taskId());
        ensureTaskCanBeModified(task);

        claimInCamunda(task.getTaskId(), command.assignee());
        String previousAssignee = task.getAssignee();
        task.setAssignee(command.assignee());
        task.setOwner(command.actionBy());
        task.setStatus("ASSIGNED");
        task.setClaimedAt(LocalDateTime.now());

        saveAssignmentHistory(task, "CLAIM", previousAssignee, command.assignee(), null, null, command.actionBy(), command.comment());
        removeCandidateIdentityLinks(task);
        saveProcessHistory(task, "CLAIM", command.actionBy(), command.comment());
        return toResult(processTaskRepository.save(task));
    }

    @Override
    public WorkflowTaskResult claimTaskByCandidate(ClaimTaskByCandidateCommand command) {
        if (command == null) {
            throw new BusinessException("Claim by candidate command is required");
        }
        if (command.taskId() == null) {
            throw new BusinessException("taskId is required");
        }
        if (isBlank(command.username())) {
            throw new BusinessException("username is required");
        }

        UserTask task = getTask(command.taskId());
        ensureTaskCanBeModified(task);
        if (!isClaimableForUsername(task, command.username())) {
            throw new BusinessException("Task is not claimable by user " + command.username());
        }

        claimInCamunda(task.getTaskId(), command.username());
        String previousAssignee = task.getAssignee();
        task.setAssignee(command.username());
        task.setOwner(command.username());
        task.setStatus("ASSIGNED");
        task.setClaimedAt(LocalDateTime.now());

        saveAssignmentHistory(task, "CLAIM_BY_CANDIDATE", previousAssignee, command.username(), null, null, command.username(), command.comment());
        removeCandidateIdentityLinks(task);
        saveProcessHistory(task, "CLAIM_BY_CANDIDATE", command.username(), command.comment());
        return toResult(processTaskRepository.save(task));
    }

    @Override
    public WorkflowTaskResult reassignTask(ReassignTaskCommand command) {
        validateClaimCommand(command == null ? null : command.taskId(), command == null ? null : command.assignee(), command == null ? null : command.actionBy());
        UserTask task = getTask(command.taskId());
        ensureTaskCanBeModified(task);

        claimInCamunda(task.getTaskId(), command.assignee());
        String previousAssignee = task.getAssignee();
        task.setAssignee(command.assignee());
        task.setStatus("ASSIGNED");
        if (task.getClaimedAt() == null) {
            task.setClaimedAt(LocalDateTime.now());
        }

        saveAssignmentHistory(task, "REASSIGN", previousAssignee, command.assignee(), null, null, command.actionBy(), command.comment());
        saveProcessHistory(task, "REASSIGN", command.actionBy(), command.comment());
        return toResult(processTaskRepository.save(task));
    }

    @Override
    public WorkflowTaskResult completeTask(CompleteTaskCommand command) {
        if (command == null) {
            throw new BusinessException("Complete task command is required");
        }
        if (command.taskId() == null) {
            throw new BusinessException("taskId is required");
        }
        if (isBlank(command.actionBy())) {
            throw new BusinessException("actionBy is required");
        }

        UserTask task = getTask(command.taskId());
        ensureTaskCanBeModified(task);

        if (command.data() != null && !command.data().isEmpty()) {
            saveDraftInCamunda(task.getTaskId(), command.data());
            saveTaskData(SaveTaskDataCommand.builder()
                    .taskId(command.taskId())
                    .changedBy(command.actionBy())
                    .data(command.data())
                    .build());
        }

        completeInCamunda(task.getTaskId(), command.data());
        task.setStatus("COMPLETED");
        task.setCompletedAt(LocalDateTime.now());
        saveAssignmentHistory(task, "COMPLETE", task.getAssignee(), task.getAssignee(), null, null, command.actionBy(), command.comment());
        saveProcessHistory(task, "COMPLETE", command.actionBy(), command.comment());
        return toResult(processTaskRepository.save(task));
    }

    @Override
    public void saveTaskData(SaveTaskDataCommand command) {
        if (command == null) {
            throw new BusinessException("Save task data command is required");
        }
        if (command.taskId() == null) {
            throw new BusinessException("taskId is required");
        }

        UserTask task = getTask(command.taskId());
        String dataJson = serialize(command.data());
        ProcessData taskData = processTaskDataRepository.findByTask_Id(command.taskId())
                .orElseGet(() -> {
                    ProcessData created = new ProcessData();
                    created.setTask(task);
                    created.setCreatedAt(LocalDateTime.now());
                    return created;
                });
        taskData.setDataJson(dataJson);
        taskData.setUpdatedAt(LocalDateTime.now());
        processTaskDataRepository.save(taskData);

        Integer version = processTaskDataHistoryRepository.findAll().stream()
                .filter(history -> history.getTask() != null && command.taskId().equals(history.getTask().getId()))
                .map(ProcessTaskDataHistory::getVersion)
                .filter(value -> value != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        ProcessTaskDataHistory history = new ProcessTaskDataHistory();
        history.setTask(task);
        history.setDataJson(dataJson);
        history.setChangedBy(command.changedBy());
        history.setChangedAt(LocalDateTime.now());
        history.setVersion(version);
        processTaskDataHistoryRepository.save(history);
    }

    @Override
    public WorkflowTaskIdentityLinkResult createTaskIdentityLink(CreateWorkflowTaskIdentityLinkCommand command) {
        validateIdentityLinkCommand(command);
        UserTask task = getTask(command.taskId());

        ProcessTaskIdentityLink identityLink = new ProcessTaskIdentityLink();
        identityLink.setTask(task);
        identityLink.setUserId(command.userId());
        identityLink.setGroupId(command.groupId());
        identityLink.setType(normalizeType(command.type()));
        identityLink.setCreatedAt(LocalDateTime.now());
        return toResult(processTaskIdentityLinkRepository.save(identityLink));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowTaskIdentityLinkResult> getTaskIdentityLinks(Long taskId) {
        requireTaskId(taskId);
        getTask(taskId);
        return processTaskIdentityLinkRepository.findByTask_IdOrderByCreatedAtAsc(taskId).stream()
                .map(this::toResult)
                .toList();
    }

    @Override
    public void deleteTaskIdentityLink(Long identityLinkId) {
        requireIdentityLinkId(identityLinkId);
        ProcessTaskIdentityLink identityLink = processTaskIdentityLinkRepository.findById(identityLinkId)
                .orElseThrow(() -> new ResourceNotFoundException("Task identity link not found with id=" + identityLinkId));
        processTaskIdentityLinkRepository.delete(identityLink);
    }

    private UserTask getTask(Long taskId) {
        UserTask task = processTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id=" + taskId));
        ensureTaskExistsInCamunda(task.getTaskId());
        return task;
    }

    private Set<String> getPendingCamundaTaskIds() {
        try {
            return camundaTaskService.getPendingTasks().stream()
                    .map(Task::getId)
                    .collect(Collectors.toSet());
        } catch (TaskListException exception) {
            throw new BusinessException("Unable to load pending tasks from Camunda");
        }
    }

    private void validateClaimCommand(Long taskId, String assignee, String actionBy) {
        if (taskId == null) {
            throw new BusinessException("taskId is required");
        }
        if (isBlank(assignee)) {
            throw new BusinessException("assignee is required");
        }
        if (isBlank(actionBy)) {
            throw new BusinessException("actionBy is required");
        }
    }

    private void ensureTaskCanBeModified(UserTask task) {
        if (isCompleted(task)) {
            throw new BusinessException("Task is already completed");
        }
    }

    private void requireTaskId(Long taskId) {
        if (taskId == null) {
            throw new BusinessException("taskId is required");
        }
    }

    private void requireIdentityLinkId(Long identityLinkId) {
        if (identityLinkId == null) {
            throw new BusinessException("identityLinkId is required");
        }
    }

    private void validateIdentityLinkCommand(CreateWorkflowTaskIdentityLinkCommand command) {
        if (command == null) {
            throw new BusinessException("Identity link command is required");
        }
        if (command.taskId() == null) {
            throw new BusinessException("taskId is required");
        }
        if (isBlank(command.type())) {
            throw new BusinessException("type is required");
        }
        if (command.userId() == null && command.groupId() == null) {
            throw new BusinessException("Either userId or groupId must be provided");
        }
        if (command.userId() != null && command.groupId() != null) {
            throw new BusinessException("Only one of userId or groupId can be provided");
        }

        String normalizedType = normalizeType(command.type());
        if (!ALLOWED_LINK_TYPES.contains(normalizedType)) {
            throw new BusinessException("Unsupported identity link type: " + command.type());
        }
    }

    private WorkflowTaskIdentityLinkResult toResult(ProcessTaskIdentityLink identityLink) {
        return WorkflowTaskIdentityLinkResult.builder()
                .id(identityLink.getId())
                .taskId(identityLink.getTask() == null ? null : identityLink.getTask().getId())
                .userId(identityLink.getUserId())
                .groupId(identityLink.getGroupId())
                .type(identityLink.getType())
                .createdAt(identityLink.getCreatedAt())
                .build();
    }

    private WorkflowTaskResult toResult(UserTask task) {
        ProcessInstance processInstance = task.getProcessInstance();
        return WorkflowTaskResult.builder()
                .id(task.getId())
                .workflowInstanceId(processInstance == null ? null : processInstance.getId())
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

    private ClaimableTaskResult toClaimableResult(UserTask task, String username) {
        ProcessInstance processInstance = task.getProcessInstance();
        return ClaimableTaskResult.builder()
                .taskId(task.getId())
                .workflowInstanceId(processInstance == null ? null : processInstance.getId())
                .businessKey(processInstance == null ? null : processInstance.getBusinessKey())
                .workflowStatus(processInstance == null || processInstance.getStatus() == null ? null : processInstance.getStatus().name())
                .currentStepCode(processInstance == null ? null : processInstance.getCurrentStepCode())
                .taskName(task.getTaskName())
                .taskCode(task.getTaskCode())
                .taskStatus(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .candidateSourceType(resolveCandidateSourceType(task, username))
                .candidateGroupCode(task.getCandidateGroup())
                .candidateGroupName(task.getCandidateGroup())
                .build();
    }

    private boolean isClaimableForUsername(UserTask task, String username) {
        if (task == null || isCompleted(task)) {
            return false;
        }

        String normalizedUsername = normalize(username);
        return normalizedUsername.equals(normalize(task.getAssignee()))
                || normalizedUsername.equals(normalize(task.getOwner()))
                || normalizedUsername.equals(normalize(task.getCandidateGroup()))
                || (isOpen(task) && hasCandidateIdentityLink(task.getId()));
    }

    private String resolveCandidateSourceType(UserTask task, String username) {
        String normalizedUsername = normalize(username);
        if (normalizedUsername.equals(normalize(task.getAssignee())) || normalizedUsername.equals(normalize(task.getOwner()))) {
            return "DIRECT";
        }
        if (normalizedUsername.equals(normalize(task.getCandidateGroup()))) {
            return "GROUP";
        }
        return hasCandidateIdentityLink(task.getId()) ? "GROUP" : null;
    }

    private boolean hasCandidateIdentityLink(Long taskId) {
        return processTaskIdentityLinkRepository.findByTask_IdOrderByCreatedAtAsc(taskId).stream()
                .anyMatch(link -> "CANDIDATE".equalsIgnoreCase(link.getType()));
    }

    private void removeCandidateIdentityLinks(UserTask task) {
        processTaskIdentityLinkRepository.findByTask_IdOrderByCreatedAtAsc(task.getId()).stream()
                .filter(link -> "CANDIDATE".equalsIgnoreCase(link.getType()))
                .forEach(processTaskIdentityLinkRepository::delete);
    }

    private void saveAssignmentHistory(
            UserTask task,
            String action,
            String fromUser,
            String toUser,
            Long fromGroup,
            Long toGroup,
            String actionBy,
            String comment) {
        UserTaskAssignmentHistory history = new UserTaskAssignmentHistory();
        history.setTask(task);
        history.setAction(action);
        history.setFromUser(fromUser);
        history.setToUser(toUser);
        history.setFromGroup(fromGroup);
        history.setToGroup(toGroup);
        history.setActionBy(actionBy);
        history.setActionAt(LocalDateTime.now());
        history.setComment(comment);
        processTaskAssignmentHistoryRepository.save(history);
    }

    private void saveProcessHistory(UserTask task, String action, String actionBy, String note) {
        ProcessHistory history = new ProcessHistory();
        history.setTask(task);
        history.setProcessInstance(task.getProcessInstance());
        history.setAction(action);
        history.setActionBy(actionBy);
        history.setActionAt(LocalDateTime.now());
        history.setNote(note);
        processHistoryRepository.save(history);
    }

    private String serialize(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data == null ? Map.of() : data);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("Unable to serialize task data");
        }
    }

    private void claimInCamunda(String taskId, String assignee) {
        try {
            camundaTaskService.claimTask(taskId, assignee);
        } catch (TaskListException exception) {
            throw new BusinessException("Unable to claim task in Camunda");
        }
    }

    private void completeInCamunda(String taskId, Map<String, Object> variables) {
        try {
            camundaTaskService.completeTask(taskId, variables == null ? Map.of() : variables);
        } catch (TaskListException exception) {
            throw new BusinessException("Unable to complete task in Camunda");
        }
    }

    private void saveDraftInCamunda(String taskId, Map<String, Object> variables) {
        try {
            camundaTaskService.saveTaskData(taskId, variables == null ? Map.of() : variables);
        } catch (TaskListException exception) {
            throw new BusinessException("Unable to save draft variables in Camunda");
        }
    }

    private void ensureTaskExistsInCamunda(String taskId) {
        if (isBlank(taskId)) {
            throw new BusinessException("Camunda taskId is required");
        }
        try {
            camundaTaskService.getTask(taskId);
        } catch (TaskListException exception) {
            throw new ResourceNotFoundException("Camunda task not found with id=" + taskId);
        }
    }

    private boolean isCompleted(UserTask task) {
        return task.getStatus() != null && "COMPLETED".equalsIgnoreCase(task.getStatus());
    }

    private boolean isOpen(UserTask task) {
        return task.getStatus() == null || "OPEN".equalsIgnoreCase(task.getStatus());
    }

    private String normalizeType(String type) {
        return type == null ? null : type.trim().toUpperCase();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
