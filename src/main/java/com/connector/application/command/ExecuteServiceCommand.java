package com.connector.application.command;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public record ExecuteServiceCommand(
        String appId,
        String serviceCode,
        String serviceVersion,
        Map<String, String> headers,
        JsonNode payload) {
}
