package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.entity.connection.SftpConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SftpConfigHandler implements ConnectionDefinitionHandler {

    private final ObjectMapper objectMapper;

    public SftpConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectionType type() {
        return ConnectionType.SFTP;
    }

    @Override
    public ConnectionConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, SftpConnectionConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid SFTP config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Serialize SFTP failed");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
                "host", "string",
                "port", "number",
                "username", "string",
                "password", "string",
                "privateKey", "string",
                "remoteDirectory", "string");
    }
}