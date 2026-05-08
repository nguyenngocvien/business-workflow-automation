package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ServiceDefinitionHandler;
import com.connector.domain.entity.service.RestServiceConfig;
import com.connector.domain.entity.service.ServiceConfig;
import com.connector.domain.enums.HttpMethodType;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestServiceConfigHandler implements ServiceDefinitionHandler {

    private final ObjectMapper objectMapper;

    public RestServiceConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceType type() {
        return ServiceType.REST;
    }

    @Override
    public ServiceConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, RestServiceConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid REST config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize REST config");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
            "type", "REST",
            "fields", Map.of(
                "url", "string",
                "method", Map.of(
                    "type", "enum",
                    "values", enumValues(HttpMethodType.class)
                ),
                "headers", "object<string,string>"
            )
        );
    }

    private <E extends Enum<E>> java.util.List<String> enumValues(Class<E> enumType) {
        return java.util.Arrays.stream(enumType.getEnumConstants())
            .map(Enum::name)
            .toList();
    }
}