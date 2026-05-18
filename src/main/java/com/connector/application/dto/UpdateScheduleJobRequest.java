package com.connector.application.dto;

import com.connector.domain.enums.JobType;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateScheduleJobRequest(

    @Size(max = 255, message = "jobName must be at most 255 characters")
    String jobName,

    JobType jobType,

    Long serviceId,
    Long pipelineId,

    @Size(max = 50, message = "cronExpression must be at most 50 characters")
    String cronExpression,

    @PositiveOrZero(message = "fixedRateMs must be greater than or equal to 0")
    Long fixedRateMs,

    Boolean enabled

) {}