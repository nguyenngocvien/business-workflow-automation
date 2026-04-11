package com.workflow.application.usecase.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ClaimableTaskResult(
    Long taskId,
    Long workflowInstanceId,
    String businessKey,
    String workflowStatus,
    String currentStepCode,
    String taskName,
    String taskCode,
    String taskStatus,
    Integer priority,
    LocalDateTime dueDate,
    LocalDateTime createdAt,
    @Schema(description = "How the user can claim the task: directly as a candidate user or through candidate group membership", example = "GROUP")
    String candidateSourceType,
    @Schema(description = "Candidate group code when the task is claimable through a group", example = "FINANCE")
    String candidateGroupCode,
    @Schema(description = "Candidate group name when the task is claimable through a group", example = "Finance Department")
    String candidateGroupName
) {
}
