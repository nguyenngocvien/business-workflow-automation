package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StepDefinitionRequest(
    @Schema(description = "Step display name", example = "Submit Request")
    @NotBlank String stepName,
    @Schema(description = "Unique step code inside the workflow", example = "SUBMIT")
    @NotBlank String stepCode,
    @Schema(description = "Step type", example = "USER_TASK")
    @NotBlank String stepType,
    @Schema(description = "Execution order of the step", example = "1")
    Integer stepOrder,
    @Schema(description = "Next step code", example = "MANAGER_APPROVAL")
    String nextStepCode,
    @Schema(description = "Conditional expression for transition", example = "amount > 1000")
    String conditionExpression,
    @Schema(description = "SLA in minutes", example = "60")
    Integer slaMinutes
) {
}
