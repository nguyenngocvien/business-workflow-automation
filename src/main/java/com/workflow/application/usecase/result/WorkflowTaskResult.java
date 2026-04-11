package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowTaskResult(
    Long id,
    Long workflowInstanceId,
    Long stepInstanceId,
    String taskName,
    String taskCode,
    String assignee,
    String owner,
    String status,
    Integer priority,
    LocalDateTime dueDate,
    LocalDateTime createdAt,
    LocalDateTime claimedAt,
    LocalDateTime completedAt
) {
}
