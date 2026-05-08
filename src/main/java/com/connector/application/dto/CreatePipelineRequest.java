package com.connector.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePipelineRequest(

    @NotBlank(message = "pipelineCode is required")
    @Size(max = 100, message = "pipelineCode must be at most 100 characters")
    String pipelineCode,

    @NotBlank(message = "pipelineName is required")
    @Size(max = 255, message = "pipelineName must be at most 255 characters")
    String pipelineName,

    String description,

    Boolean active

) {}