package com.connector.application.dto.response;

import java.time.LocalDateTime;

public record EcJobExecutionResponse(
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
