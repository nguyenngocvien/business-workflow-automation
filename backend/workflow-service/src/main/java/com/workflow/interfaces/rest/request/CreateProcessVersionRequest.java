package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProcessVersionRequest {

    @Schema(description = "Business process key used to group versions", example = "purchase-approval")
    @NotBlank
    private String processKey;

    @Schema(description = "Display name of the process version", example = "Purchase Approval v2")
    @NotBlank
    private String name;

    @Schema(description = "BPMN XML content for the version", example = "<definitions>...</definitions>")
    @NotBlank
    private String bpmnXml;

    @Schema(description = "Optional description of this version", example = "Adds manager approval step")
    private String description;

    @Schema(description = "Username or identifier of the creator", example = "admin")
    private String createdBy;
}
