package com.connector.application.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record UpdateJobExecutionRequest(

    LocalDateTime endTime,

    @Size(max = 50, message = "status must be at most 50 characters")
    String status,

    String responseData,

    String errorMessage

) {}