package com.connector.application.result;

import java.util.List;
import java.util.Map;

import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;

public record ExecuteServiceResult(
    Long serviceId,
    String appId,
    String serviceCode,
    String serviceVersion,
    ServiceType serviceType,
    int statusCode,
    Map<String, List<String>> responseHeaders,
    JsonNode body
) {

    public ExecuteServiceResponse toResponseDto() {
        return new ExecuteServiceResponse(
            serviceId,
            appId,
            serviceCode,
            serviceVersion,
            serviceType,
            statusCode,
            responseHeaders,
            body
        );
    }
}
