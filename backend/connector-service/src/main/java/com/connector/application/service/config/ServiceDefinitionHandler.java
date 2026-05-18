package com.connector.application.service.config;

import java.util.Map;

import com.connector.domain.entity.service.ServiceConfig;
import com.connector.domain.enums.ServiceType;

public interface ServiceDefinitionHandler {

    ServiceType type();

    ServiceConfig deserialize(String json);

    String serialize(Object config);

    Map<String, Object> schema();
}