package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ClaimTaskRequest(
    @Schema(description = "Assignee username", example = "bob")
    @NotBlank String assignee,
    @Schema(description = "User performing the action", example = "team-lead")
    String actionBy,
    @Schema(description = "Claim note", example = "Assign task to Bob")
    String comment
) {
}
