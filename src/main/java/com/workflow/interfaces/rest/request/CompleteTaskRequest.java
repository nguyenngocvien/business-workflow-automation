package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CompleteTaskRequest(
    @Schema(description = "User completing the task", example = "bob")
    @NotBlank String actionBy,
    @Schema(description = "Completion note", example = "Approved")
    String comment,
    @Schema(description = "Business data payload stored into task data", example = "{\"approved\":true,\"approvalLevel\":\"MANAGER\"}")
    Map<String, Object> data
) {
}
