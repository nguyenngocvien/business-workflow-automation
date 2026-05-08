package com.connector.application.result;

import java.time.LocalDateTime;

public record PipelineResult(
    Long id,
    String pipelineCode,
    String pipelineName,
    String description,
    Boolean active,
    LocalDateTime createdAt
) {
}
