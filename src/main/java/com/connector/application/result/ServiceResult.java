package com.connector.application.result;

import java.time.LocalDateTime;

import com.connector.domain.entity.service.ServiceConfig;
import com.connector.domain.enums.ServiceType;

public record ServiceResult(
    Long id,
    String serviceCode,
    String serviceName,
    ServiceType serviceType,
    String serviceVersion,
    String appId,
    Long connectionId,
    String configJson,
    ServiceConfig config,
    Integer timeoutMs,
    Integer retryCount,
    Boolean active,
    Boolean logEnable,
    String createdBy,
    String updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
