package com.connector.application.dto;

import jakarta.validation.constraints.Size;

public record UpdatePipelineRequest(

    @Size(max = 255, message = "pipelineName must be at most 255 characters")
    String pipelineName,

    String description,

    Boolean active

) {}