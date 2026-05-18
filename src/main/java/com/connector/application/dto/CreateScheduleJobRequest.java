package com.connector.application.dto;

import com.connector.domain.enums.JobType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateScheduleJobRequest(

    @NotBlank(message = "jobCode is required")
    @Size(max = 100, message = "jobCode must be at most 100 characters")
    String jobCode,

    @NotBlank(message = "jobName is required")
    @Size(max = 255, message = "jobName must be at most 255 characters")
    String jobName,

    @NotNull(message = "jobType is required")
    JobType jobType,

    Long serviceId,
    Long pipelineId,

    @Size(max = 50, message = "cronExpression must be at most 50 characters")
    String cronExpression,

    @PositiveOrZero(message = "fixedRateMs must be greater than or equal to 0")
    Long fixedRateMs,

    Boolean enabled

) {}