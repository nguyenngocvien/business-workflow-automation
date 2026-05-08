package com.connector.application.dto;

import java.time.LocalDateTime;

import com.connector.domain.entity.connection.ConnectionConfig;
import com.connector.domain.enums.ConnectionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConnectionRequest(
    @NotBlank(message = "connectionCode is required")
    @Size(max = 100, message = "connectionCode must be at most 100 characters")
    String connectionCode,
    @NotBlank(message = "connectionName is required")
    @Size(max = 255, message = "connectionName must be at most 255 characters")
    String connectionName,
    @NotNull(message = "connectionType is required")
    ConnectionType connectionType,
    String configJson,
    ConnectionConfig config,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
