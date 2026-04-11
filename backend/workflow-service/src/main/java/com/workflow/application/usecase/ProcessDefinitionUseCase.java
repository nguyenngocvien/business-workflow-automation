package com.workflow.application.usecase;

import com.workflow.application.usecase.command.DeployWorkflowDefinitionCommand;
import com.workflow.application.usecase.command.DeployWorkflowBpmnCommand;
import com.workflow.application.usecase.command.UploadWorkflowBpmnCommand;
import com.workflow.application.usecase.result.WorkflowBpmnResult;
import com.workflow.application.usecase.result.WorkflowDefinitionResult;
import com.workflow.application.usecase.result.WorkflowDefinitionDeploymentResult;
import java.util.List;

public interface ProcessDefinitionUseCase {

    WorkflowDefinitionResult deployDefinition(DeployWorkflowDefinitionCommand command);

    WorkflowBpmnResult uploadBpmn(UploadWorkflowBpmnCommand command);

    WorkflowDefinitionDeploymentResult deployBpmn(DeployWorkflowBpmnCommand command);

    List<WorkflowDefinitionDeploymentResult> getDeploymentHistory(Long definitionId);

    WorkflowDefinitionResult getDefinition(Long definitionId);

    List<WorkflowDefinitionResult> getDefinitionsByWorkflowKey(String workflowKey);
}
