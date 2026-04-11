package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeployWorkflowBpmnRequest(
    @Schema(description = "User who deploys the BPMN to Camunda", example = "architect")
    @NotBlank String deployedBy
) {
}
