package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.entity.connection.SoapConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SoapConfigHandler implements ConnectionDefinitionHandler {

    private final ObjectMapper objectMapper;

    public SoapConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectionType type() {
        return ConnectionType.SOAP;
    }

    @Override
    public ConnectionConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, SoapConnectionConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid SOAP config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Serialize SOAP failed");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
                "wsdlUrl", "string",
                "endpoint", "string",
                "soapAction", "string",
                "headers", "object<string,string>");
    }
}