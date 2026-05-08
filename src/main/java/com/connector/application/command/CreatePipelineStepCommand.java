package com.connector.application.command;

public record CreatePipelineStepCommand(

    Long pipelineId,
    Integer stepOrder,
    Long serviceId,
    String stepName,
    String requestTransform,
    String responseTransform,
    Boolean continueOnError

) {}