package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DeployWorkflowDefinitionRequest(
    @Schema(description = "Application name", example = "ERP")
    @NotBlank String applicationName,
    @Schema(description = "Workflow display name", example = "Purchase Approval")
    @NotBlank String workflowName,
    @Schema(description = "Unique workflow key", example = "PURCHASE_APPROVAL")
    @NotBlank String workflowKey,
    @Schema(description = "Workflow description", example = "Approval flow for purchase request")
    String description,
    @Schema(description = "User who deploys the workflow", example = "architect")
    String createdBy,
    @ArraySchema(schema = @Schema(implementation = StepDefinitionRequest.class))
    @NotEmpty List<@Valid StepDefinitionRequest> steps
) {
}
