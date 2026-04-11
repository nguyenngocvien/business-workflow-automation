package com.connector.application.dto.response;

import java.time.LocalDateTime;

public record EcPipelineResponse(
    Long id,
    String pipelineCode,
    String pipelineName,
    String description,
    Boolean active,
    LocalDateTime createdAt
) {
}
