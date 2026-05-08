package com.connector.application.command;

import java.time.LocalDateTime;

public record CreateJobExecutionCommand(

    Long jobId,
    LocalDateTime startTime,
    String requestData

) {}