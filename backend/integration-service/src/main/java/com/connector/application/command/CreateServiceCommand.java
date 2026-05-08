package com.connector.application.command;

import java.util.Map;

import com.connector.domain.enums.ServiceType;

public record CreateServiceCommand(

    String serviceCode,
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
    String createdBy

) {}