package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWorkflowTaskIdentityLinkRequest(
    @NotNull Long taskId,
    Long userId,
    Long groupId,
    @Schema(example = "CANDIDATE")
    @NotBlank String type
) {
}
