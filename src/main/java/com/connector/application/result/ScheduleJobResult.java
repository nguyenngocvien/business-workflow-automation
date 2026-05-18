package com.connector.application.result;

import java.time.LocalDateTime;

import com.connector.domain.enums.JobType;

public record ScheduleJobResult(
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
