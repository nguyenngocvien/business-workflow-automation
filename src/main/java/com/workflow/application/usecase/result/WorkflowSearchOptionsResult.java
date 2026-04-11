package com.workflow.application.usecase.result;

import java.util.List;
import lombok.Builder;

@Builder
public record WorkflowSearchOptionsResult(
    List<WorkflowLookupItemResult> workflows,
    List<WorkflowLookupItemResult> steps,
    List<String> statuses
) {
}
