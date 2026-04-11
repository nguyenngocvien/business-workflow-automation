package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowStepDefinitionResult(
    Long id,
    String stepName,
    String stepCode,
    String stepType,
    Integer stepOrder,
    String nextStepCode,
    String conditionExpression,
    Integer slaMinutes,
    LocalDateTime createdAt
) {
}
