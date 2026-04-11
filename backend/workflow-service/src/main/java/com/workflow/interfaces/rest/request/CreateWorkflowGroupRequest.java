package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateWorkflowGroupRequest(
    @Schema(example = "FINANCE")
    @NotBlank String groupCode,
    @Schema(example = "Finance")
    @NotBlank String groupName,
    String description,
    Long parentGroupId,
    Boolean active,
    @Schema(example = "admin")
    String createdBy
) {
}
