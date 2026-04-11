package com.connector.domain.entity.serviceconfig;

import com.connector.domain.enums.DbOperationType;

public record DbServiceConfig(
    String sql,
    DbOperationType operation
) implements ServiceConfig {
}
