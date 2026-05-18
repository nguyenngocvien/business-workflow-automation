package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ServiceDefinitionHandler;
import com.connector.domain.entity.service.EmailServiceConfig;
import com.connector.domain.entity.service.ServiceConfig;
import com.connector.domain.enums.MailProtocolType;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EmailServiceConfigHandler implements ServiceDefinitionHandler {

    private final ObjectMapper objectMapper;

    public EmailServiceConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceType type() {
        return ServiceType.EMAIL;
    }

    @Override
    public ServiceConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, EmailServiceConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid EMAIL config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize EMAIL config");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.ofEntries(
                Map.entry("host", "string"),
                Map.entry("port", "number"),
                Map.entry("username", "string"),
                Map.entry("password", "string"),
                Map.entry("from", "string"),
                Map.entry("protocol", Map.of(
                        "type", "enum",
                        "values", enumValues(MailProtocolType.class))),
                Map.entry("auth", "boolean"),
                Map.entry("starttls", "boolean"),
                Map.entry("ssl", "boolean"),
                Map.entry("templateType", "string"),
                Map.entry("templateCode", "string"),
                Map.entry("subject", "string"),
                Map.entry("body", "string"),
                Map.entry("html", "boolean"),
                Map.entry("to", "array<string>"),
                Map.entry("cc", "array<string>"),
                Map.entry("bcc", "array<string>"),
                Map.entry("properties", "object<string,string>"));
    }

    private <E extends Enum<E>> java.util.List<String> enumValues(Class<E> enumType) {
        return java.util.Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .toList();
    }
}