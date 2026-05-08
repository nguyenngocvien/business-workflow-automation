package com.connector.application.command;

public record CreatePipelineCommand(

    String pipelineCode,
    String pipelineName,
    String description,
    Boolean active

) {}