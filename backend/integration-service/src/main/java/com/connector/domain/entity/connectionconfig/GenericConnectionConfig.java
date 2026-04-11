package com.connector.domain.entity.connectionconfig;

import java.util.Map;

public record GenericConnectionConfig(
    Map<String, Object> values
) implements ConnectionConfig {
}
