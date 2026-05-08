package com.connector.application.result;

import java.time.LocalDateTime;

public record PipelineStepResult(
    Long id,
    Long pipelineId,
    Integer stepOrder,
    Long serviceId,
    String stepName,
    String requestTransform,
    String responseTransform,
    Boolean continueOnError,
    LocalDateTime createdAt
) {
}
