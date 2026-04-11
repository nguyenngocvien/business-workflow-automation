package com.workflow.interfaces.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record SaveTaskDataRequest(
    @Schema(description = "User updating task data", example = "bob")
    String changedBy,
    @Schema(description = "Task data payload", example = "{\"remark\":\"Need supporting document\"}")
    @NotNull Map<String, Object> data
) {
}
