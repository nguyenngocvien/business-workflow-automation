package com.workflow.interfaces.rest;

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
import com.workflow.interfaces.rest.request.ClaimTaskByCandidateRequest;
import com.workflow.interfaces.rest.request.ClaimTaskRequest;
import com.workflow.interfaces.rest.request.CompleteTaskRequest;
import com.workflow.interfaces.rest.request.CreateWorkflowTaskIdentityLinkRequest;
import com.workflow.interfaces.rest.request.ReassignTaskRequest;
import com.workflow.interfaces.rest.request.SaveTaskDataRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "User Tasks", description = "APIs for claiming, reassigning, and completing user tasks")
public class ProcessTaskController {

    private final ProcessTaskUseCase processTaskUseCase;

    public ProcessTaskController(
        ProcessTaskUseCase processTaskUseCase) 
    {
        this.processTaskUseCase = processTaskUseCase;
    }

    @GetMapping("/claimable")
    @Operation(summary = "Get claimable tasks by user", description = "Return tasks that the user can claim directly as a candidate user or indirectly through candidate group membership. Tasks that are already claimed by another assignee or already completed are excluded")
    public List<ClaimableTaskResult> getClaimableTasks(@RequestParam String username) {
        return processTaskUseCase.getClaimableTasks(username);
    }

    @PatchMapping("/{taskId}/claim")
    @Operation(summary = "Claim task", description = "Assign a task to an assignee, mark it as claimed, and remove all CANDIDATE identity links of the task after a successful claim")
    public WorkflowTaskResult claimTask(@PathVariable Long taskId, @Valid @RequestBody ClaimTaskRequest request) {
        ClaimTaskCommand command = ClaimTaskCommand.builder()
            .taskId(taskId)
            .assignee(request.assignee())
            .actionBy(request.actionBy())
            .comment(request.comment())
            .build();
        return processTaskUseCase.claimTask(command);
    }

    @PatchMapping("/{taskId}/claim-by-candidate")
    @Operation(summary = "Claim task by candidate", description = "Allow claim only when the user is a valid candidate directly or through candidate group membership. After a successful claim, all CANDIDATE identity links of the task are removed")
    public WorkflowTaskResult claimTaskByCandidate(@PathVariable Long taskId, @Valid @RequestBody ClaimTaskByCandidateRequest request) {
        ClaimTaskByCandidateCommand command = ClaimTaskByCandidateCommand.builder()
            .taskId(taskId)
            .username(request.username())
            .comment(request.comment())
            .build();
        return processTaskUseCase.claimTaskByCandidate(command);
    }

    @PatchMapping("/{taskId}/reassign")
    @Operation(summary = "Reassign task", description = "Reassign an existing task to another assignee and write assignment history")
    public WorkflowTaskResult reassignTask(@PathVariable Long taskId, @Valid @RequestBody ReassignTaskRequest request) {
        ReassignTaskCommand command = ReassignTaskCommand.builder()
            .taskId(taskId)
            .assignee(request.assignee())
            .actionBy(request.actionBy())
            .comment(request.comment())
            .build();
        return processTaskUseCase.reassignTask(command);
    }

    @PatchMapping("/{taskId}/complete")
    @Operation(summary = "Complete task", description = "Complete a task, optionally save task data, and move workflow to the next step")
    public WorkflowTaskResult completeTask(@PathVariable Long taskId, @Valid @RequestBody CompleteTaskRequest request) {
        CompleteTaskCommand command = CompleteTaskCommand.builder()
            .taskId(taskId)
            .actionBy(request.actionBy())
            .comment(request.comment())
            .data(request.data())
            .build();
        return processTaskUseCase.completeTask(command);
    }

    @PatchMapping("/{taskId}/data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Save task data", description = "Save task data without completing the task")
    public void saveTaskData(@PathVariable Long taskId, @Valid @RequestBody SaveTaskDataRequest request) {
        SaveTaskDataCommand command = SaveTaskDataCommand.builder()
            .taskId(taskId)
            .changedBy(request.changedBy())
            .data(request.data())
            .build();
        processTaskUseCase.saveTaskData(command);
    }

    @GetMapping("/{taskId}/identity-links")
    @Operation(summary = "List task identity links", description = "Get all wf_task_identity_link records for a workflow task")
    public List<WorkflowTaskIdentityLinkResult> getTaskIdentityLinks(@PathVariable Long taskId) {
        return processTaskUseCase.getTaskIdentityLinks(taskId);
    }

    @PostMapping("/task-identity-links")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create task identity link", description = "Assign candidate user or candidate group to a workflow task")
    public WorkflowTaskIdentityLinkResult createTaskIdentityLink(@Valid @RequestBody CreateWorkflowTaskIdentityLinkRequest request) {
        return processTaskUseCase.createTaskIdentityLink(CreateWorkflowTaskIdentityLinkCommand.builder()
            .taskId(request.taskId())
            .userId(request.userId())
            .groupId(request.groupId())
            .type(request.type())
            .build());
    }

    @DeleteMapping("/task-identity-links/{identityLinkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task identity link", description = "Delete a wf_task_identity_link record")
    public void deleteTaskIdentityLink(@PathVariable Long identityLinkId) {
        processTaskUseCase.deleteTaskIdentityLink(identityLinkId);
    }
}
