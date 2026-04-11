package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record StartProcessRequest(
    @Schema(description = "Workflow key to start", example = "PURCHASE_APPROVAL")
    @NotBlank String workflowKey,
    @Schema(description = "Business key that links workflow to business object", example = "PO-2026-0001")
    String businessKey,
    @Schema(description = "User who starts the workflow", example = "alice")
    @NotBlank String startedBy,
    @Schema(description = "Detailed form data from frontend to initialize Camunda process variables")
    Map<String, Object> formData
) {
}
