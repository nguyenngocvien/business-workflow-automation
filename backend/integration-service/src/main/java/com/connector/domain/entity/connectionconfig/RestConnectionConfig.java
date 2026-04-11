package com.connector.domain.entity.connectionconfig;

import java.util.Map;

public record RestConnectionConfig(
    String baseUrl,
    Map<String, String> defaultHeaders,
    Integer connectTimeoutMs,
    Integer readTimeoutMs
) implements ConnectionConfig {
}
