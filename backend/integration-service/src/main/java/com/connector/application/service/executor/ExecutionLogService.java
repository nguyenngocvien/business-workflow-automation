package com.connector.application.service.executor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.connector.domain.entity.LogEntity;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.repository.LogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ExecutionLogService {

    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    public ExecutionLogService(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    public void saveSuccess(
            ServiceEntity service,
            String traceId,
            String correlationId,
            LocalDateTime requestTime,
            HttpStatusCode statusCode,
            HttpHeaders requestHeaders,
            JsonNode requestBody,
            JsonNode requestAfterTransform,
            JsonNode responseBody,
            JsonNode responseAfterTransform) {
        LogEntity log = baseLog(service, traceId, correlationId, requestTime);
        log.setResponseTime(LocalDateTime.now());
        log.setStatusCode(statusCode.value());
        log.setRequestHeaders(toJson(maskHeaders(requestHeaders.toSingleValueMap())));
        log.setRequestBody(maskJsonNode(requestBody));
        log.setRequestAfterTransform(maskJsonNode(requestAfterTransform));
        log.setResponseBody(maskJsonNode(responseBody));
        log.setResponseAfterTransform(maskJsonNode(responseAfterTransform));
        logRepository.save(log);
    }

    public void saveFailure(
            ServiceEntity service,
            String traceId,
            String correlationId,
            LocalDateTime requestTime,
            HttpStatusCode statusCode,
            HttpHeaders requestHeaders,
            JsonNode requestBody,
            JsonNode requestAfterTransform,
            JsonNode responseBody,
            JsonNode responseAfterTransform,
            Exception ex) {
        LogEntity log = baseLog(service, traceId, correlationId, requestTime);
        log.setResponseTime(LocalDateTime.now());
        log.setStatusCode(statusCode != null ? statusCode.value() : 500);
        log.setRequestHeaders(toJson(maskHeaders(requestHeaders.toSingleValueMap())));
        log.setRequestBody(maskJsonNode(requestBody));
        log.setRequestAfterTransform(maskJsonNode(requestAfterTransform));
        log.setResponseBody(maskJsonNode(responseBody));
        log.setResponseAfterTransform(maskJsonNode(responseAfterTransform));
        log.setErrorMessage(ex.getMessage());
        log.setStacktrace(stackTraceOf(ex));
        logRepository.save(log);
    }

    private LogEntity baseLog(ServiceEntity service, String traceId, String correlationId, LocalDateTime requestTime) {
        LogEntity log = new LogEntity();
        log.setService(service);
        log.setTraceId(traceId);
        log.setCorrelationId(correlationId);
        log.setRequestTime(requestTime);
        log.setCreatedAt(requestTime);
        return log;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to serialize log value");
        }
    }

    public String maskJsonNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            JsonNode masked = maskSensitiveNode(node.deepCopy());
            return objectMapper.writeValueAsString(masked);
        } catch (Exception ex) {
            try {
                return objectMapper.writeValueAsString(node);
            } catch (Exception inner) {
                throw new IllegalArgumentException("Body is not serializable");
            }
        }
    }

    private JsonNode maskSensitiveNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return node;
        }

        if (node.isObject()) {
            ObjectNode newObj = objectMapper.createObjectNode();

            node.properties().forEach(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (isSensitiveKey(key)) {
                    newObj.put(key, "******");
                } else {
                    newObj.set(key, maskSensitiveNode(value));
                }
            });

            return newObj;
        }

        if (node.isArray()) {
            ArrayNode newArr = objectMapper.createArrayNode();
            node.forEach(child -> newArr.add(maskSensitiveNode(child)));
            return newArr;
        }

        return node;
    }

    private Map<String, String> maskHeaders(Map<String, String> headers) {
        Map<String, String> masked = new LinkedHashMap<>();
        headers.forEach((key, value) -> masked.put(key, isSensitiveKey(key) ? "******" : value));
        return masked;
    }

    private boolean isSensitiveKey(String key) {
        String normalized = key == null ? "" : key.toLowerCase();
        return normalized.contains("authorization") || normalized.contains("password");
    }

    private String stackTraceOf(Exception ex) {
        StringBuilder builder = new StringBuilder(ex.toString());
        for (StackTraceElement element : ex.getStackTrace()) {
            builder.append(System.lineSeparator()).append("\tat ").append(element);
        }
        return builder.toString();
    }
}
