package com.connector.application.dto.response;

import java.time.LocalDateTime;

public record EcPipelineStepResponse(
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
