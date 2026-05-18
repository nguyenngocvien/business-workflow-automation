package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.entity.connection.RestConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestConfigHandler implements ConnectionDefinitionHandler {

    private final ObjectMapper objectMapper;

    public RestConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectionType type() {
        return ConnectionType.REST;
    }

    @Override
    public ConnectionConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, RestConnectionConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid REST config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Serialize REST failed");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
                "baseUrl", "string",
                "defaultHeaders", "object<string,string>",
                "connectTimeoutMs", "number",
                "readTimeoutMs", "number");
    }
}