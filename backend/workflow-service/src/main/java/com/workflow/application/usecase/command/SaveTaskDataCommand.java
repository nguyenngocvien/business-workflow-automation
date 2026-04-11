package com.workflow.application.usecase.command;

import java.util.Map;
import lombok.Builder;

@Builder
public record SaveTaskDataCommand(
    Long taskId,
    String changedBy,
    Map<String, Object> data
) {
}
