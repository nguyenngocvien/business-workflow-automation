package com.connector.domain.entity.serviceconfig;

import java.util.Map;

public record GenericServiceConfig(
    Map<String, Object> values
) implements ServiceConfig {
}
