package com.connector.application.dto.response;

import java.time.LocalDateTime;

import com.connector.domain.entity.connectionconfig.ConnectionConfig;
import com.connector.domain.enums.ConnectionType;

public record EcConnectionResponse(
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
