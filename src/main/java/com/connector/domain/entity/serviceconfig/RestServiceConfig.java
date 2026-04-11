package com.connector.domain.entity.serviceconfig;

import java.util.Map;

import com.connector.domain.enums.HttpMethodType;

public record RestServiceConfig(
    String url,
    HttpMethodType method,
    Map<String, String> headers
) implements ServiceConfig {
}
