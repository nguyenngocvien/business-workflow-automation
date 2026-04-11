package com.workflow.application.usecase.command;

import lombok.Builder;

@Builder
public record ClaimTaskByCandidateCommand(
    Long taskId,
    String username,
    String comment
) {
}
