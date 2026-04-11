package com.workflow.application.usecase;

import com.workflow.application.usecase.command.StartProcessCommand;
import com.workflow.application.usecase.result.ProcessInstanceResult;
import com.workflow.application.usecase.result.WorkflowProgressResult;
import com.workflow.application.usecase.result.WorkflowSearchOptionsResult;
import com.workflow.application.usecase.result.WorkflowSearchResult;
import java.util.List;

public interface ProcessInstanceUseCase {

    ProcessInstanceResult startProcess(StartProcessCommand command);

    ProcessInstanceResult getProcessInstance(Long workflowInstanceId);

    WorkflowProgressResult getProgress(Long workflowInstanceId);

    WorkflowSearchOptionsResult getProcessSearchOptions(String workflowKey);

    List<WorkflowSearchResult> searchProcesses(
            String applicationName,
            String workflowKey,
            String currentStepCode,
            String status,
            String businessKey,
            String assignee);
}
