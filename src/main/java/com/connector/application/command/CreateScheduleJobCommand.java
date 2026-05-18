package com.connector.application.command;

import com.connector.domain.enums.JobType;

public record CreateScheduleJobCommand(

    String jobCode,
    String jobName,
    JobType jobType,
    Long serviceId,
    Long pipelineId,
    String cronExpression,
    Long fixedRateMs,
    Boolean enabled

) {}