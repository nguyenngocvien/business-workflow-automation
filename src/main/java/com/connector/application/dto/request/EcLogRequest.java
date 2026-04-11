package com.connector.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record EcLogRequest(
    Long serviceId,
    @Size(max = 100, message = "traceId must be at most 100 characters")
    String traceId,
    @Size(max = 100, message = "correlationId must be at most 100 characters")
    String correlationId,
    @NotNull(message = "requestTime is required")
    LocalDateTime requestTime,
    LocalDateTime responseTime,
    @PositiveOrZero(message = "durationMs must be greater than or equal to 0")
    Long durationMs,
    Integer statusCode,
    String requestHeaders,
    String requestBody,
    String requestAfterTransform,
    String responseBody,
    String responseAfterTransform,
    String errorMessage,
    String stacktrace,
    LocalDateTime createdAt
) {
}
