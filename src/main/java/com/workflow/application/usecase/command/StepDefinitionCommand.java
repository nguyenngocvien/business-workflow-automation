package com.workflow.application.usecase.command;

import lombok.Builder;

@Builder
public record StepDefinitionCommand(
    String stepName,
    String stepCode,
    String stepType,
    Integer stepOrder,
    String nextStepCode,
    String conditionExpression,
    Integer slaMinutes
) {
}
