package com.connector.application.command;

import com.connector.domain.enums.JobType;

public record UpdateScheduleJobCommand(

    Long id,

    String jobName,
    JobType jobType,
    Long serviceId,
    Long pipelineId,
    String cronExpression,
    Long fixedRateMs,
    Boolean enabled

) {}