package com.connector.domain.entity.connection;

public record DbConnectionConfig(
    String url,
    String username,
    String password,
    String driverClassName,
    String driver
) implements ConnectionConfig {
}
