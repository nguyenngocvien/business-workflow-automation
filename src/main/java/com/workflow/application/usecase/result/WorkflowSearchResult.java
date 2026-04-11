package com.workflow.application.usecase.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowSearchResult(
    Long wfInstanceId,
    String businessKey,
    String workflowStatus,
    String currentStepCode,
    String assignee,
    @Schema(description = "Full name of the assignee resolved from wf_user.full_name", example = "Nguyen Van A")
    String assigneeFullName,
    String taskStatus,
    LocalDateTime reminderDate,
    LocalDateTime taskCreatedAt,
    LocalDateTime completedAt
) {
}
