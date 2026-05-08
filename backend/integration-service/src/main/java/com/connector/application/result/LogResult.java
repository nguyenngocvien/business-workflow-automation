package com.connector.application.result;

import java.time.LocalDateTime;

public record LogResult(
    Long id,
    Long serviceId,
    String traceId,
    String correlationId,
    LocalDateTime requestTime,
    LocalDateTime responseTime,
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
