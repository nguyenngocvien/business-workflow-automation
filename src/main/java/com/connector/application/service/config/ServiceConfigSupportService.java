package com.connector.application.service.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.connector.domain.entity.serviceconfig.DbServiceConfig;
import com.connector.domain.entity.serviceconfig.EmailServiceConfig;
import com.connector.domain.entity.serviceconfig.GenericServiceConfig;
import com.connector.domain.entity.serviceconfig.RestServiceConfig;
import com.connector.domain.entity.serviceconfig.ServiceConfig;
import com.connector.domain.enums.DbOperationType;
import com.connector.domain.enums.HttpMethodType;
import com.connector.domain.enums.MailProtocolType;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ServiceConfigSupportService {

    private final ObjectMapper objectMapper;

    public ServiceConfigSupportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ServiceConfig deserialize(ServiceType serviceType, String configJson) {
        if (!StringUtils.hasText(configJson)) {
            return null;
        }
        try {
            return switch (serviceType) {
                case REST -> objectMapper.readValue(configJson, RestServiceConfig.class);
                case DB -> objectMapper.readValue(configJson, DbServiceConfig.class);
                case EMAIL -> objectMapper.readValue(configJson, EmailServiceConfig.class);
                default -> new GenericServiceConfig(
                    objectMapper.readValue(configJson, new TypeReference<LinkedHashMap<String, Object>>() {})
                );
            };
        } catch (Exception ex) {
            throw new IllegalArgumentException("configJson is invalid for serviceType " + serviceType + ": " + ex.getMessage());
        }
    }

    public String serialize(ServiceType serviceType, Object config, String fallbackConfigJson) {
        if (config == null) {
            return fallbackConfigJson;
        }
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception ex) {
            throw new IllegalArgumentException("config is not serializable for serviceType " + serviceType);
        }
    }

    public Map<String, Object> schema(ServiceType serviceType) {
        return switch (serviceType) {
            case REST -> Map.of(
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
            case DB -> Map.of(
                "type", "DB",
                "fields", Map.of(
                    "sql", "string",
                    "operation", Map.of(
                        "type", "enum",
                        "values", enumValues(DbOperationType.class)
                    )
                )
            );
            case EMAIL -> Map.of(
                "type", "EMAIL",
                "fields", Map.ofEntries(
                    Map.entry("host", "string"),
                    Map.entry("port", "number"),
                    Map.entry("username", "string"),
                    Map.entry("password", "string"),
                    Map.entry("from", "string"),
                    Map.entry("protocol", Map.of(
                        "type", "enum",
                        "values", enumValues(MailProtocolType.class)
                    )),
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
                    Map.entry("properties", "object<string,string>")
                )
            );
            default -> Map.of(
                "type", serviceType != null ? serviceType.name() : "UNKNOWN",
                "fields", Map.of("values", "object")
            );
        };
    }

    private <E extends Enum<E>> java.util.List<String> enumValues(Class<E> enumType) {
        return java.util.Arrays.stream(enumType.getEnumConstants())
            .map(Enum::name)
            .toList();
    }
}
