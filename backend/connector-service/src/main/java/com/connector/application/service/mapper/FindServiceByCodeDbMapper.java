package com.connector.application.service.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class FindServiceByCodeDbMapper implements ServiceExecutionMapper {

    private final ObjectMapper objectMapper;

    public FindServiceByCodeDbMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.DB;
    }

    @Override
    public String supportedServiceCode() {
        return "FIND_SERVICE_BY_CODE";
    }

    @Override
    public ExecuteServiceCommand mapRequest(ServiceEntity service, ExecuteServiceCommand command) {

        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        JsonNode payload = command.payload();

        String serviceCode = text(payload, "serviceCode");
        if (!StringUtils.hasText(serviceCode)) {
            throw new IllegalArgumentException("payload.serviceCode is required for FIND_SERVICE_BY_CODE");
        }

        ObjectNode mappedPayload = objectMapper.createObjectNode();
        ObjectNode params = objectMapper.createObjectNode();
        params.put("serviceCode", serviceCode);
        mappedPayload.set("params", params);

        return new ExecuteServiceCommand(
                command.appId(),
                command.serviceCode(),
                command.serviceVersion(),
                command.headers(),
                mappedPayload);
    }

    @Override
    public ExecuteServiceResult mapResponse(ServiceEntity service, ExecuteServiceResult result) {
        JsonNode body = result.body();
        JsonNode firstRow = extractFirstRow(body);

        ObjectNode mappedBody = objectMapper.createObjectNode();
        mappedBody.put("found", firstRow != null && !firstRow.isNull());
        if (firstRow != null && !firstRow.isNull()) {
            mappedBody.set("service", firstRow);
        } else {
            mappedBody.putNull("service");
        }

        return new ExecuteServiceResult(
                result.serviceId(),
                result.appId(),
                result.serviceCode(),
                result.serviceVersion(),
                result.serviceType(),
                result.statusCode(),
                result.responseHeaders(),
                mappedBody);
    }

    private JsonNode extractFirstRow(JsonNode body) {
        if (body == null || body.isNull()) {
            return null;
        }

        JsonNode rows = body.get("rows");
        if (rows instanceof ArrayNode arrayNode && !arrayNode.isEmpty()) {
            return arrayNode.get(0);
        }
        return null;
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            return null;
        }
        JsonNode value = node.get(fieldName);
        return value != null ? value.asText() : null;
    }
}
