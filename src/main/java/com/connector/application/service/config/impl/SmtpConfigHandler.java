package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.entity.connection.SmtpConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.connector.domain.enums.MailProtocolType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SmtpConfigHandler implements ConnectionDefinitionHandler {

    private final ObjectMapper objectMapper;

    public SmtpConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectionType type() {
        return ConnectionType.SMTP;
    }

    @Override
    public ConnectionConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, SmtpConnectionConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid SMTP config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Serialize SMTP failed");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
                "host", "string",
                "port", "number",
                "username", "string",
                "password", "string",
                "protocol", Map.of(
                        "type", "enum",
                        "values", enumValues(MailProtocolType.class)),
                "auth", "boolean",
                "starttls", "boolean",
                "ssl", "boolean",
                "from", "string");
    }

    private <E extends Enum<E>> java.util.List<String> enumValues(Class<E> enumType) {
        return java.util.Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .toList();
    }
}