package com.connector.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record EcPipelineRequest(
    @Size(max = 100, message = "pipelineCode must be at most 100 characters")
    String pipelineCode,
    @Size(max = 255, message = "pipelineName must be at most 255 characters")
    String pipelineName,
    String description,
    Boolean active,
    LocalDateTime createdAt
) {
}
