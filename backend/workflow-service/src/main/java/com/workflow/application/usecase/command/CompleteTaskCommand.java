package com.workflow.application.usecase.command;

import java.util.Map;
import lombok.Builder;

@Builder
public record CompleteTaskCommand(
    Long taskId,
    String actionBy,
    String comment,
    Map<String, Object> data
) {
}
