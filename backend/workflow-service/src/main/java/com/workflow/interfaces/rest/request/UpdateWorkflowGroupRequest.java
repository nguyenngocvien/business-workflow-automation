package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateWorkflowGroupRequest(
    @Schema(example = "Finance")
    String groupName,
    String description,
    Long parentGroupId,
    Boolean active,
    @Schema(example = "admin")
    String updatedBy
) {
}
