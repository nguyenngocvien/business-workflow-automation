package com.connector.application.command;

public record UpdatePipelineCommand(

    Long id,

    String pipelineName,
    String description,
    Boolean active

) {}