package com.workflow.infrastructure.ibm;

import com.fasterxml.jackson.databind.JsonNode;
import com.workflow.application.port.out.serializer.JsonSerializer;
import com.workflow.interfaces.config.IbmBpmProperties;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class IbmBpmClient {

    private static final String CSRF_HEADER = "BPMCSRFToken";
    private static final String LOGIN_PATH = "system/login";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final HttpClient httpClient;
    private final JsonSerializer jsonSerializer;
    private final IbmBpmProperties properties;
    private final AtomicReference<CsrfToken> csrfTokenRef = new AtomicReference<>();

    public JsonNode get(String path, Map<String, ?> queryParams) {
        return send("GET", path, queryParams, null);
    }

    public JsonNode post(String path, Map<String, ?> queryParams, Object body) {
        return send("POST", path, queryParams, body);
    }

    private JsonNode send(String method, String path, Map<String, ?> queryParams, Object body) {
        HttpRequest request = buildRequest(method, path, queryParams, body, false);
        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 403 && isCsrfExpired(response.body())) {
            csrfTokenRef.set(null);
            request = buildRequest(method, path, queryParams, body, true);
            response = sendRequest(request);
        }

        if (response.statusCode() >= 400) {
            throw new IbmBpmClientException("IBM BPM request failed with HTTP " + response.statusCode() + ": " + response.body());
        }

        return readJson(response.body());
    }

    private HttpRequest buildRequest(
        String method,
        String path,
        Map<String, ?> queryParams,
        Object body,
        boolean forceRefreshToken
    ) {
        URI uri = buildUri(path, queryParams);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(properties.getRequestTimeout())
            .header("Accept", JSON_CONTENT_TYPE);

        String authorization = basicAuthHeader();
        if (StringUtils.hasText(authorization)) {
            builder.header("Authorization", authorization);
        }

        if (!LOGIN_PATH.equals(normalizePath(path))) {
            builder.header(CSRF_HEADER, getCsrfToken(forceRefreshToken));
        }

        if ("POST".equalsIgnoreCase(method)) {
            builder.header("Content-Type", JSON_CONTENT_TYPE);
            builder.POST(HttpRequest.BodyPublishers.ofString(writeJson(body), StandardCharsets.UTF_8));
        } else {
            builder.GET();
        }

        return builder.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IbmBpmClientException("IBM BPM request was interrupted", exception);
        } catch (IOException exception) {
            throw new IbmBpmClientException("Unable to call IBM BPM", exception);
        }
    }

    private URI buildUri(String path, Map<String, ?> queryParams) {
        String normalizedBase = ensureTrailingSlash(properties.getBaseUrl());
        String normalizedPath = normalizePath(path);

        StringBuilder uri = new StringBuilder(normalizedBase).append(normalizedPath);
        String query = buildQueryString(queryParams);
        if (!query.isBlank()) {
            uri.append('?').append(query);
        }
        return URI.create(uri.toString());
    }

    private String buildQueryString(Map<String, ?> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "";
        }

        return queryParams.entrySet().stream()
            .filter(entry -> entry.getValue() != null && !String.valueOf(entry.getValue()).isBlank())
            .map(entry -> encode(entry.getKey()) + "=" + encode(String.valueOf(entry.getValue())))
            .reduce((left, right) -> left + "&" + right)
            .orElse("");
    }

    private String getCsrfToken(boolean forceRefresh) {
        CsrfToken cached = csrfTokenRef.get();
        if (!forceRefresh && cached != null && cached.isValid()) {
            return cached.value();
        }

        synchronized (csrfTokenRef) {
            cached = csrfTokenRef.get();
            if (!forceRefresh && cached != null && cached.isValid()) {
                return cached.value();
            }

            CsrfToken refreshed = login();
            csrfTokenRef.set(refreshed);
            return refreshed.value();
        }
    }

    private CsrfToken login() {
        JsonNode response = post(LOGIN_PATH, Map.of(), Map.of(
            "refresh-groups", properties.isRefreshGroups(),
            "requested-lifetime", properties.getRequestedLifetimeSeconds()
        ));

        String tokenValue = firstText(response, "csrf_token", "csrfToken");
        if (!StringUtils.hasText(tokenValue)) {
            throw new IbmBpmClientException("IBM BPM login response does not contain csrf_token");
        }

        long expiresAt = Instant.now().getEpochSecond() + Math.max(60L, properties.getRequestedLifetimeSeconds() - 60L);
        return new CsrfToken(tokenValue, expiresAt);
    }

    private boolean isCsrfExpired(String body) {
        try {
            JsonNode root = readJson(body);
            String errorNumber = firstText(root, "error_number", "errorNumber");
            return "CWTBG0651E".equalsIgnoreCase(errorNumber);
        } catch (IbmBpmClientException exception) {
            return false;
        }
    }

    private JsonNode readJson(String body) {
        try {
            return jsonSerializer.fromJson(body, JsonNode.class);
        } catch (Exception exception) {
            throw new IbmBpmClientException("Unable to parse IBM BPM response as JSON", exception);
        }
    }

    private String writeJson(Object body) {
        try {
            return jsonSerializer.toJson(body == null ? Map.of() : body);
        } catch (Exception exception) {
            throw new IbmBpmClientException("Unable to serialize IBM BPM request body", exception);
        }
    }

    private String basicAuthHeader() {
        String username = properties.getUsername();
        String password = properties.getPassword();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return "";
        }

        String raw = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    private String ensureTrailingSlash(String value) {
        return value.endsWith("/") ? value : value + "/";
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String firstText(JsonNode node, String... fieldNames) {
        if (node == null || fieldNames == null) {
            return null;
        }
        for (String fieldName : fieldNames) {
            JsonNode value = node.get(fieldName);
            if (value != null && !value.isNull() && value.isTextual()) {
                return value.asText();
            }
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }

    private record CsrfToken(String value, long expiresAtEpochSecond) {
        boolean isValid() {
            return StringUtils.hasText(value) && Instant.now().getEpochSecond() < expiresAtEpochSecond;
        }
    }
}
