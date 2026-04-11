package com.workflow.application.usecase.result;

import lombok.Builder;

@Builder
public record WorkflowLookupItemResult(
    String code,
    String name
) {
}
