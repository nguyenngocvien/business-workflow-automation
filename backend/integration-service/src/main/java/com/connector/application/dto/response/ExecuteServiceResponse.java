package com.connector.application.dto.response;

import java.util.List;
import java.util.Map;

import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;

public record ExecuteServiceResponse(
    Long serviceId,
    String appId,
    String serviceCode,
    String serviceVersion,
    ServiceType serviceType,
    int statusCode,
    Map<String, List<String>> responseHeaders,
    JsonNode body
) {
}
