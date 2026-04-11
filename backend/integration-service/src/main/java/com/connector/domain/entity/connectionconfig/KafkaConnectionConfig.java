package com.connector.domain.entity.connectionconfig;

import java.util.Map;

import com.connector.domain.enums.KafkaSecurityProtocolType;

public record KafkaConnectionConfig(
    String bootstrapServers,
    String clientId,
    KafkaSecurityProtocolType securityProtocol,
    Map<String, String> properties
) implements ConnectionConfig {
}
