package com.connector.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record EcJobExecutionRequest(
    Long jobId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    @Size(max = 50, message = "status must be at most 50 characters")
    String status,
    String requestData,
    String responseData,
    String errorMessage,
    LocalDateTime createdAt
) {
}
