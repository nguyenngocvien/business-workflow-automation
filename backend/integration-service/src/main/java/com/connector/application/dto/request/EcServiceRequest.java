package com.connector.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.connector.domain.entity.serviceconfig.ServiceConfig;
import com.connector.domain.enums.ServiceType;

public record EcServiceRequest(
    @NotBlank(message = "serviceCode is required")
    @Size(max = 100, message = "serviceCode must be at most 100 characters")
    String serviceCode,
    @Size(max = 255, message = "serviceName must be at most 255 characters")
    String serviceName,
    ServiceType serviceType,
    @Size(max = 50, message = "serviceVersion must be at most 50 characters")
    String serviceVersion,
    @Size(max = 100, message = "appId must be at most 100 characters")
    String appId,
    Long connectionId,
    String configJson,
    ServiceConfig config,
    @PositiveOrZero(message = "timeoutMs must be greater than or equal to 0")
    Integer timeoutMs,
    @PositiveOrZero(message = "retryCount must be greater than or equal to 0")
    Integer retryCount,
    Boolean active,
    Boolean logEnable,
    @Size(max = 100, message = "createdBy must be at most 100 characters")
    String createdBy,
    @Size(max = 100, message = "updatedBy must be at most 100 characters")
    String updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
