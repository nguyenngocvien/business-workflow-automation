package com.connector.domain.entity.service;

import java.util.Map;

public record GenericServiceConfig(
    Map<String, Object> values
) implements ServiceConfig {
}
