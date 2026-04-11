package com.connector.application.service.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.domain.entity.EcService;
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
    public ExecuteServiceRequest mapRequest(EcService service, ExecuteServiceRequest request) {
        JsonNode payload = request != null ? request.payload() : null;
        String serviceCode = text(payload, "serviceCode");
        if (!StringUtils.hasText(serviceCode)) {
            throw new IllegalArgumentException("payload.serviceCode is required for FIND_SERVICE_BY_CODE");
        }

        ObjectNode mappedPayload = objectMapper.createObjectNode();
        ObjectNode params = objectMapper.createObjectNode();
        params.put("serviceCode", serviceCode);
        mappedPayload.set("params", params);

        return new ExecuteServiceRequest(
            request != null ? request.headers() : null,
            mappedPayload
        );
    }

    @Override
    public ExecuteServiceResponse mapResponse(EcService service, ExecuteServiceResponse response) {
        JsonNode body = response.body();
        JsonNode firstRow = extractFirstRow(body);

        ObjectNode mappedBody = objectMapper.createObjectNode();
        mappedBody.put("found", firstRow != null && !firstRow.isNull());
        if (firstRow != null && !firstRow.isNull()) {
            mappedBody.set("service", firstRow);
        } else {
            mappedBody.putNull("service");
        }

        return new ExecuteServiceResponse(
            response.serviceId(),
            response.appId(),
            response.serviceCode(),
            response.serviceVersion(),
            response.serviceType(),
            response.statusCode(),
            response.responseHeaders(),
            mappedBody
        );
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
