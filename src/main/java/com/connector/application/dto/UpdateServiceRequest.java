package com.connector.application.dto;

import java.util.Map;

import com.connector.domain.enums.ServiceType;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateServiceRequest(

    @Size(max = 255)
    String serviceName,

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
    String updatedBy

) {}