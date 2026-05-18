package com.connector.application.service.config;

import java.util.Map;

import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.enums.ConnectionType;

public interface ConnectionDefinitionHandler {

    ConnectionType type();

    ConnectionConfig deserialize(String json);

    String serialize(Object config);

    Map<String, Object> schema();
}