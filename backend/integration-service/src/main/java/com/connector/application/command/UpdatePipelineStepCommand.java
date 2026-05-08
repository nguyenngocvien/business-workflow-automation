package com.connector.application.command;

public record UpdatePipelineStepCommand(

    Long id,

    Integer stepOrder,
    Long serviceId,
    String stepName,
    String requestTransform,
    String responseTransform,
    Boolean continueOnError

) {}