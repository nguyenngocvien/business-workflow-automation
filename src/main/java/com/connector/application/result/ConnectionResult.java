package com.connector.application.result;

import java.time.LocalDateTime;

import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.enums.ConnectionType;

public record ConnectionResult(
    Long id,
    String connectionCode,
    String connectionName,
    ConnectionType connectionType,
    String configJson,
    ConnectionConfig config,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
