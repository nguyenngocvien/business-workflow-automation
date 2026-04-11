package com.connector.application.dto.request;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public record ExecuteServiceRequest(
    Map<String, String> headers,
    JsonNode payload
) {
}
