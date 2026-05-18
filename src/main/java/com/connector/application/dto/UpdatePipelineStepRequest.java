package com.connector.application.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdatePipelineStepRequest(

    @PositiveOrZero(message = "stepOrder must be greater than or equal to 0")
    Integer stepOrder,

    Long serviceId,

    @Size(max = 255, message = "stepName must be at most 255 characters")
    String stepName,

    String requestTransform,

    String responseTransform,

    Boolean continueOnError

) {}