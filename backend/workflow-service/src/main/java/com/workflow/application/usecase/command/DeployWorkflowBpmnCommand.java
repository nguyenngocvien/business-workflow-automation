package com.workflow.application.usecase.command;

import lombok.Builder;

@Builder
public record DeployWorkflowBpmnCommand(
    Long definitionId,
    String deployedBy
) {
}
