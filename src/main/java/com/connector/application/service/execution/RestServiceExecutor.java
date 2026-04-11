package com.connector.application.service.execution;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestServiceExecutor implements TypedServiceExecutor {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public RestServiceExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.REST;
    }

    @Override
    public ExecuteServiceResponse execute(EcService service, ExecuteServiceRequest request) {
        JsonNode config = parseConfig(service.getConfigJson());
        String url = text(config, "url");
        if (!StringUtils.hasText(url)) {
            throw new IllegalArgumentException("configJson.url is required for REST service execution");
        }

        String methodName = text(config, "method");
        HttpMethod method = StringUtils.hasText(methodName)
            ? HttpMethod.valueOf(methodName.toUpperCase())
            : HttpMethod.POST;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        mergeConfigHeaders(config, headers);
        mergeRequestHeaders(request, headers);

        String requestBody = stringifyBody(request != null ? request.payload() : null);
        String traceId = extractOrGenerate(headers, "X-Trace-Id");
        String correlationId = extractOrGenerate(headers, "X-Correlation-Id");
        headers.set("X-Trace-Id", traceId);
        headers.set("X-Correlation-Id", correlationId);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(URI.create(url), method, entity, String.class);
            JsonNode responseBody = parseBody(response.getBody());

            return new ExecuteServiceResponse(
                service.getId(),
                service.getAppId(),
                service.getServiceCode(),
                service.getServiceVersion(),
                service.getServiceType(),
                response.getStatusCode().value(),
                toResponseHeaders(response.getHeaders()),
                responseBody
            );
        } catch (RestClientResponseException ex) {
            JsonNode responseBody = parseBody(ex.getResponseBodyAsString());
            throw new ServiceExecutionException(
                "Service execution failed: " + ex.getMessage(),
                ex,
                ex.getStatusCode(),
                responseBody
            );
        } catch (RestClientException ex) {
            throw new ServiceExecutionException(
                "Service execution failed: " + ex.getMessage(),
                ex,
                null,
                null
            );
        }
    }

    private JsonNode parseConfig(String configJson) {
        try {
            return objectMapper.readTree(configJson);
        } catch (Exception ex) {
            throw new IllegalArgumentException("configJson is not valid JSON");
        }
    }

    private void mergeConfigHeaders(JsonNode config, HttpHeaders headers) {
        JsonNode headerNode = config.get("headers");
        if (headerNode == null || !headerNode.isObject()) {
            return;
        }
        headerNode.fields().forEachRemaining(entry -> headers.add(entry.getKey(), entry.getValue().asText()));
    }

    private void mergeRequestHeaders(ExecuteServiceRequest request, HttpHeaders headers) {
        if (request == null || request.headers() == null) {
            return;
        }
        request.headers().forEach(headers::set);
    }

    private String stringifyBody(JsonNode payload) {
        if (payload == null || payload.isNull()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Request payload is not serializable");
        }
    }

    private JsonNode parseBody(String body) {
        if (!StringUtils.hasText(body)) {
            return objectMapper.nullNode();
        }
        try {
            return objectMapper.readTree(body);
        } catch (Exception ex) {
            return objectMapper.getNodeFactory().textNode(body);
        }
    }

    private Map<String, List<String>> toResponseHeaders(HttpHeaders headers) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        headers.forEach((key, value) -> result.put(key, List.copyOf(value)));
        return result;
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        return value != null ? value.asText() : null;
    }

    private String extractOrGenerate(HttpHeaders headers, String headerName) {
        String value = headers.getFirst(headerName);
        return StringUtils.hasText(value) ? value : UUID.randomUUID().toString();
    }
}
