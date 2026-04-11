package com.workflow.application.usecase.command;

import java.util.List;
import lombok.Builder;

@Builder
public record DeployWorkflowDefinitionCommand(
    String applicationName,
    String workflowName,
    String workflowKey,
    String description,
    String createdBy,
    List<StepDefinitionCommand> steps
) {
}
