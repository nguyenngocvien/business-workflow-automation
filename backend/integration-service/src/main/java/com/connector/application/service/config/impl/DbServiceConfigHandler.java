package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ServiceDefinitionHandler;
import com.connector.domain.entity.service.DbServiceConfig;
import com.connector.domain.entity.service.ServiceConfig;
import com.connector.domain.enums.DbOperationType;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DbServiceConfigHandler implements ServiceDefinitionHandler {

    private final ObjectMapper objectMapper;

    public DbServiceConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceType type() {
        return ServiceType.DB;
    }

    @Override
    public ServiceConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, DbServiceConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid DB config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize DB config");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
            "type", "DB",
            "fields", Map.of(
                "sql", "string",
                "operation", Map.of(
                    "type", "enum",
                    "values", enumValues(DbOperationType.class)
                )
            )
        );
    }

    private <E extends Enum<E>> java.util.List<String> enumValues(Class<E> enumType) {
        return java.util.Arrays.stream(enumType.getEnumConstants())
            .map(Enum::name)
            .toList();
    }
}