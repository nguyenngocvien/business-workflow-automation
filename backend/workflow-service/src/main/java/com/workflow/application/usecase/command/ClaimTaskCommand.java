package com.workflow.application.usecase.command;

import lombok.Builder;

@Builder
public record ClaimTaskCommand(
    Long taskId,
    String assignee,
    String actionBy,
    String comment
) {
}
