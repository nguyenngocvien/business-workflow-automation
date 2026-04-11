package com.connector.application.service.execution;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.connector.domain.entity.EcLog;
import com.connector.domain.entity.EcService;
import com.connector.domain.repository.EcLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ExecutionLogService {

    private final EcLogRepository logRepository;
    private final ObjectMapper objectMapper;

    public ExecutionLogService(EcLogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    public void saveSuccess(
        EcService service,
        String traceId,
        String correlationId,
        LocalDateTime requestTime,
        HttpStatusCode statusCode,
        HttpHeaders requestHeaders,
        JsonNode requestBody,
        JsonNode requestAfterTransform,
        JsonNode responseBody,
        JsonNode responseAfterTransform
    ) {
        EcLog log = baseLog(service, traceId, correlationId, requestTime);
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
        EcService service,
        String traceId,
        String correlationId,
        LocalDateTime requestTime,
        HttpStatusCode statusCode,
        HttpHeaders requestHeaders,
        JsonNode requestBody,
        JsonNode requestAfterTransform,
        JsonNode responseBody,
        JsonNode responseAfterTransform,
        Exception ex
    ) {
        EcLog log = baseLog(service, traceId, correlationId, requestTime);
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

    private EcLog baseLog(EcService service, String traceId, String correlationId, LocalDateTime requestTime) {
        EcLog log = new EcLog();
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
            node.fields().forEachRemaining(entry -> {
                if (isSensitiveKey(entry.getKey())) {
                    ((ObjectNode) node).put(entry.getKey(), "******");
                } else {
                    maskSensitiveNode(entry.getValue());
                }
            });
            return node;
        }
        if (node.isArray()) {
            node.forEach(this::maskSensitiveNode);
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
