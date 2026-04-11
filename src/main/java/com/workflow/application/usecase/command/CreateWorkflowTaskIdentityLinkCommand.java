package com.workflow.application.usecase.command;

import lombok.Builder;

@Builder
public record CreateWorkflowTaskIdentityLinkCommand(
    Long taskId,
    Long userId,
    Long groupId,
    String type
) {
}
