package com.connector.application.dto;

import java.util.Map;

import com.connector.domain.enums.ServiceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateServiceRequest(

    @NotBlank(message = "serviceCode is required")
    @Size(max = 100)
    String serviceCode,

    @Size(max = 255)
    String serviceName,

    @NotNull(message = "serviceType is required")
    ServiceType serviceType,

    @Size(max = 50)
    String serviceVersion,

    @Size(max = 100)
    String appId,

    Long connectionId,

    Map<String, Object> config,

    @PositiveOrZero
    Integer timeoutMs,

    @PositiveOrZero
    Integer retryCount,

    Boolean active,
    Boolean logEnable,

    @Size(max = 100)
    String createdBy

) {}