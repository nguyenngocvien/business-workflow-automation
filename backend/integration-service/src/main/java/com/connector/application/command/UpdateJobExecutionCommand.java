package com.connector.application.command;

import java.time.LocalDateTime;

public record UpdateJobExecutionCommand(

    Long id,

    LocalDateTime endTime,
    String status,
    String responseData,
    String errorMessage

) {}