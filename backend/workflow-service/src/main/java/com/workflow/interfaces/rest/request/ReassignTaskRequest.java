package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReassignTaskRequest(
    @Schema(description = "New assignee username", example = "bob")
    @NotBlank String assignee,
    @Schema(description = "User performing the reassignment", example = "manager")
    String actionBy,
    @Schema(description = "Reassignment note", example = "Move task to Bob")
    String comment
) {
}
