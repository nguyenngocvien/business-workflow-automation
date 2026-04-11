package com.workflow.application.usecase;

import com.workflow.application.usecase.command.ClaimTaskByCandidateCommand;
import com.workflow.application.usecase.command.ClaimTaskCommand;
import com.workflow.application.usecase.command.CompleteTaskCommand;
import com.workflow.application.usecase.command.CreateWorkflowTaskIdentityLinkCommand;
import com.workflow.application.usecase.command.ReassignTaskCommand;
import com.workflow.application.usecase.command.SaveTaskDataCommand;
import com.workflow.application.usecase.result.ClaimableTaskResult;
import com.workflow.application.usecase.result.WorkflowTaskIdentityLinkResult;
import com.workflow.application.usecase.result.WorkflowTaskResult;

import java.util.List;

public interface ProcessTaskUseCase {

    List<ClaimableTaskResult> getClaimableTasks(String username);

    WorkflowTaskResult claimTask(ClaimTaskCommand command);

    WorkflowTaskResult claimTaskByCandidate(ClaimTaskByCandidateCommand command);

    WorkflowTaskResult reassignTask(ReassignTaskCommand command);

    WorkflowTaskResult completeTask(CompleteTaskCommand command);

    void saveTaskData(SaveTaskDataCommand command);

    WorkflowTaskIdentityLinkResult createTaskIdentityLink(CreateWorkflowTaskIdentityLinkCommand command);

    List<WorkflowTaskIdentityLinkResult> getTaskIdentityLinks(Long taskId);

    void deleteTaskIdentityLink(Long identityLinkId);
}
