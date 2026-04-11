package com.workflow.application.usecase.command;

import java.util.Map;
import lombok.Builder;

@Builder
public record StartProcessCommand(
    String workflowKey,
    String businessKey,
    String startedBy,
    Map<String, Object> formData
) {
}
