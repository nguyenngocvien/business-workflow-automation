package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateWorkflowUserRequest(
    @Schema(example = "alice")
    @NotBlank String username,
    @Schema(example = "Alice Nguyen")
    String fullName,
    @Schema(example = "alice@example.com")
    String email,
    @Schema(example = "true")
    Boolean active
) {
}
