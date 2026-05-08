package com.connector.application.service.config.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.entity.connection.KafkaConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.connector.domain.enums.KafkaSecurityProtocolType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaConfigHandler implements ConnectionDefinitionHandler {

    private final ObjectMapper objectMapper;

    public KafkaConfigHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectionType type() {
        return ConnectionType.KAFKA;
    }

    @Override
    public ConnectionConfig deserialize(String json) {
        try {
            return objectMapper.readValue(json, KafkaConnectionConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid KAFKA config");
        }
    }

    @Override
    public String serialize(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Serialize KAFKA failed");
        }
    }

    @Override
    public Map<String, Object> schema() {
        return Map.of(
                "bootstrapServers", "string",
                    "clientId", "string",
                    "securityProtocol", Map.of(
                        "type", "enum",
                        "values", enumValues(KafkaSecurityProtocolType.class)
                    ),
                    "properties", "object<string,string>");
    }

    private <E extends Enum<E>> java.util.List<String> enumValues(Class<E> enumType) {
        return java.util.Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .toList();
    }
}