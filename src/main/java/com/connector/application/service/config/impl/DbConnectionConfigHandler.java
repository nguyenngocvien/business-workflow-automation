package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.entity.connection.DbConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DbConnectionConfigHandler implements ConnectionDefinitionHandler {

    private final ObjectMapper objectMapper;

    public DbConnectionConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectionType type() {
        return ConnectionType.DB;
    }

    @Override
    public ConnectionConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, DbConnectionConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid DB config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Serialize DB failed");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
                "url", "string",
                "username", "string",
                "password", "string",
                "driverClassName", "string",
                "driver", "string");
    }
}