package com.connector.application.service.executor;

import java.util.Map;

import org.springframework.http.HttpStatusCode;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.exception.ServiceExecutionException;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.domain.entity.ServiceEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractServiceExecutor implements ServiceExecutor {

    protected final ObjectMapper objectMapper;

    protected AbstractServiceExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ExecuteServiceResult execute(ServiceEntity service, ExecuteServiceCommand request) {

        try {
            JsonNode config = parseJson(service.getConfigJson());

            Object prepared = prepare(service, config, request);

            Object result = invoke(prepared);

            JsonNode responseBody = buildBody(result);

            return buildResponse(service, responseBody);

        } catch (Exception ex) {
            throw new ServiceExecutionException(
                "Execution failed: " + ex.getMessage(),
                ex,
                HttpStatusCode.valueOf(500),
                objectMapper.getNodeFactory().textNode(ex.getMessage())
            );
        }
    }

    // ===== TEMPLATE STEPS =====

    protected abstract Object prepare(ServiceEntity service, JsonNode config, ExecuteServiceCommand request);

    protected abstract Object invoke(Object prepared);

    protected abstract JsonNode buildBody(Object result);

    // ===== COMMON =====

    protected ExecuteServiceResult buildResponse(ServiceEntity service, JsonNode body) {
        return new ExecuteServiceResult(
            service.getId(),
            service.getAppId(),
            service.getServiceCode(),
            service.getServiceVersion(),
            service.getServiceType(),
            200,
            Map.of(),
            body
        );
    }

    protected JsonNode parseJson(String raw) {
        try {
            return objectMapper.readTree(raw);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    protected String text(JsonNode node, String field) {
        return node != null && node.has(field) ? node.get(field).asText() : null;
    }
}