package com.connector.domain.entity.service;

import com.connector.domain.enums.DbOperationType;

public record DbServiceConfig(
    String sql,
    DbOperationType operation
) implements ServiceConfig {
}
