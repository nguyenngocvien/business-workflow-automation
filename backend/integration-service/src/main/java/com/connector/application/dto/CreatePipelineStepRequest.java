package com.connector.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreatePipelineStepRequest(

    @NotNull(message = "pipelineId is required")
    Long pipelineId,

    @NotNull(message = "stepOrder is required")
    @PositiveOrZero(message = "stepOrder must be greater than or equal to 0")
    Integer stepOrder,

    @NotNull(message = "serviceId is required")
    Long serviceId,

    @Size(max = 255, message = "stepName must be at most 255 characters")
    String stepName,

    String requestTransform,

    String responseTransform,

    Boolean continueOnError

) {}