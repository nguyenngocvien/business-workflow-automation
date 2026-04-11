package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ClaimTaskByCandidateRequest(
    @Schema(description = "Candidate username claiming the task for themselves", example = "alice")
    @NotBlank String username,
    @Schema(description = "Claim note", example = "Candidate user claims the task")
    String comment
) {
}
