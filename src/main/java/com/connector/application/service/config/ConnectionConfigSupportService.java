package com.connector.application.service.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.connector.domain.entity.connectionconfig.ConnectionConfig;
import com.connector.domain.entity.connectionconfig.DbConnectionConfig;
import com.connector.domain.entity.connectionconfig.GenericConnectionConfig;
import com.connector.domain.entity.connectionconfig.KafkaConnectionConfig;
import com.connector.domain.entity.connectionconfig.RestConnectionConfig;
import com.connector.domain.entity.connectionconfig.SftpConnectionConfig;
import com.connector.domain.entity.connectionconfig.SmtpConnectionConfig;
import com.connector.domain.entity.connectionconfig.SoapConnectionConfig;
import com.connector.domain.enums.ConnectionType;
import com.connector.domain.enums.KafkaSecurityProtocolType;
import com.connector.domain.enums.MailProtocolType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConnectionConfigSupportService {

    private final ObjectMapper objectMapper;

    public ConnectionConfigSupportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ConnectionConfig deserialize(ConnectionType connectionType, String configJson) {
        if (!StringUtils.hasText(configJson)) {
            return null;
        }
        try {
            return switch (connectionType) {
                case DB -> objectMapper.readValue(configJson, DbConnectionConfig.class);
                case REST -> objectMapper.readValue(configJson, RestConnectionConfig.class);
                case SOAP -> objectMapper.readValue(configJson, SoapConnectionConfig.class);
                case SFTP -> objectMapper.readValue(configJson, SftpConnectionConfig.class);
                case SMTP -> objectMapper.readValue(configJson, SmtpConnectionConfig.class);
                case KAFKA -> objectMapper.readValue(configJson, KafkaConnectionConfig.class);
                default -> new GenericConnectionConfig(
                    objectMapper.readValue(configJson, new TypeReference<LinkedHashMap<String, Object>>() {})
                );
            };
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                "configJson is invalid for connectionType " + connectionType + ": " + ex.getMessage()
            );
        }
    }

    public String serialize(ConnectionType connectionType, Object config, String fallbackConfigJson) {
        if (config == null) {
            return fallbackConfigJson;
        }
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception ex) {
            throw new IllegalArgumentException("config is not serializable for connectionType " + connectionType);
        }
    }

    public Map<String, Object> schema(ConnectionType connectionType) {
        return switch (connectionType) {
            case DB -> Map.of(
                "type", "DB",
                "fields", Map.of(
                    "url", "string",
                    "username", "string",
                    "password", "string",
                    "driverClassName", "string",
                    "driver", "string"
                )
            );
            case REST -> Map.of(
                "type", "REST",
                "fields", Map.of(
                    "baseUrl", "string",
                    "defaultHeaders", "object<string,string>",
                    "connectTimeoutMs", "number",
                    "readTimeoutMs", "number"
                )
            );
            case SOAP -> Map.of(
                "type", "SOAP",
                "fields", Map.of(
                    "wsdlUrl", "string",
                    "endpoint", "string",
                    "soapAction", "string",
                    "headers", "object<string,string>"
                )
            );
            case SFTP -> Map.of(
                "type", "SFTP",
                "fields", Map.of(
                    "host", "string",
                    "port", "number",
                    "username", "string",
                    "password", "string",
                    "privateKey", "string",
                    "remoteDirectory", "string"
                )
            );
            case SMTP -> Map.of(
                "type", "SMTP",
                "fields", Map.of(
                    "host", "string",
                    "port", "number",
                    "username", "string",
                    "password", "string",
                    "protocol", Map.of(
                        "type", "enum",
                        "values", enumValues(MailProtocolType.class)
                    ),
                    "auth", "boolean",
                    "starttls", "boolean",
                    "ssl", "boolean",
                    "from", "string"
                )
            );
            case KAFKA -> Map.of(
                "type", "KAFKA",
                "fields", Map.of(
                    "bootstrapServers", "string",
                    "clientId", "string",
                    "securityProtocol", Map.of(
                        "type", "enum",
                        "values", enumValues(KafkaSecurityProtocolType.class)
                    ),
                    "properties", "object<string,string>"
                )
            );
            default -> Map.of(
                "type", connectionType != null ? connectionType.name() : "UNKNOWN",
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
