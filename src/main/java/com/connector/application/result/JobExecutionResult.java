package com.connector.application.result;

import java.time.LocalDateTime;

public record JobExecutionResult(
    Long id,
    Long jobId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String status,
    String requestData,
    String responseData,
    String errorMessage,
    LocalDateTime createdAt
) {
}
