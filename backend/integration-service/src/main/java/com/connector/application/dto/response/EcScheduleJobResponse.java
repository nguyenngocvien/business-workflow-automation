package com.connector.application.dto.response;

import java.time.LocalDateTime;

import com.connector.domain.enums.JobType;

public record EcScheduleJobResponse(
    Long id,
    String jobCode,
    String jobName,
    JobType jobType,
    Long serviceId,
    Long pipelineId,
    String cronExpression,
    Long fixedRateMs,
    Boolean enabled,
    LocalDateTime lastRunTime,
    LocalDateTime nextRunTime,
    LocalDateTime createdAt
) {
}
