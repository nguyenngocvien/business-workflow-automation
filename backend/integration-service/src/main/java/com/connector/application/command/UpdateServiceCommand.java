package com.connector.application.command;

import java.util.Map;

import com.connector.domain.enums.ServiceType;

public record UpdateServiceCommand(

    Long id,

    String serviceName,
    ServiceType serviceType,
    String serviceVersion,
    String appId,
    Long connectionId,
    Map<String, Object> config,
    Integer timeoutMs,
    Integer retryCount,
    Boolean active,
    Boolean logEnable,
    String updatedBy

) {}