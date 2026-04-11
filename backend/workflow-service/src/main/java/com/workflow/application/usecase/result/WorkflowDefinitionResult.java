package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkflowDefinitionResult(
    Long id,
    String applicationName,
    String workflowName,
    String workflowKey,
    Integer version,
    String description,
    Boolean active,
    String createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<WorkflowStepDefinitionResult> steps
) {
}
