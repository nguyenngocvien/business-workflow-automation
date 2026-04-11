package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkflowStepInstanceResult(
    Long id,
    Long stepDefinitionId,
    String stepCode,
    String status,
    String processedBy,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    List<WorkflowTaskResult> tasks
) {
}
