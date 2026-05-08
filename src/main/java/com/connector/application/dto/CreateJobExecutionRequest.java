package com.connector.application.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record CreateJobExecutionRequest(

    @NotNull(message = "jobId is required")
    Long jobId,

    LocalDateTime startTime,

    String requestData

) {}