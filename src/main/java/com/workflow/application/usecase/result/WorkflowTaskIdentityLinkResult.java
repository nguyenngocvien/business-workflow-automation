package com.workflow.application.usecase.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowTaskIdentityLinkResult(
    Long id,
    Long taskId,
    Long userId,
    @Schema(example = "alice")
    String username,
    @Schema(example = "Alice Nguyen")
    String userFullName,
    Long groupId,
    @Schema(example = "FINANCE")
    String groupCode,
    @Schema(example = "Finance")
    String groupName,
    @Schema(example = "CANDIDATE")
    String type,
    LocalDateTime createdAt
) {
}
