package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ProcessInstanceResult(
    Long id,
    Long workflowDefinitionId,
    String businessKey,
    String status,
    String currentStepCode,
    String startedBy,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    List<WorkflowStepInstanceResult> stepInstances
) {
}
