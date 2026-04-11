package com.connector.application.service.execution;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.domain.entity.EcConnection;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ConnectionType;
import com.connector.domain.enums.DbOperationType;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DbServiceExecutor implements TypedServiceExecutor {

    private final ObjectMapper objectMapper;

    public DbServiceExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.DB;
    }

    @Override
    public ExecuteServiceResponse execute(EcService service, ExecuteServiceRequest request) {
        JsonNode serviceConfig = parseJson(service.getConfigJson(), "service configJson");
        EcConnection connection = service.getConnection();
        if (connection == null) {
            throw new IllegalArgumentException("DB service requires connection_id");
        }
        if (connection.getConnectionType() != ConnectionType.DB) {
            throw new IllegalArgumentException("Connection type must be DB for DB service execution");
        }

        JsonNode connectionConfig = parseJson(connection.getConfigJson(), "connection configJson");
        String sql = text(serviceConfig, "sql");
        if (!StringUtils.hasText(sql)) {
            throw new IllegalArgumentException("service configJson.sql is required for DB service execution");
        }

        try {
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(createDataSource(connectionConfig));
            MapSqlParameterSource params = toSqlParameters(request != null ? request.payload() : null);
            String operation = firstNonBlank(text(serviceConfig, "operation"), detectOperation(sql));
            DbOperationType dbOperation = resolveOperation(operation);

            JsonNode responseBody;
            if (dbOperation == DbOperationType.UPDATE || dbOperation == DbOperationType.INSERT || dbOperation == DbOperationType.DELETE) {
                int affectedRows = jdbcTemplate.update(sql, params);
                responseBody = objectMapper.createObjectNode()
                    .put("operation", dbOperation.name())
                    .put("affectedRows", affectedRows);
            } else {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
                ArrayNode data = objectMapper.valueToTree(rows);
                ObjectNode result = objectMapper.createObjectNode();
                result.put("operation", DbOperationType.QUERY.name());
                result.put("rowCount", rows.size());
                result.set("rows", data);
                responseBody = result;
            }

            return new ExecuteServiceResponse(
                service.getId(),
                service.getAppId(),
                service.getServiceCode(),
                service.getServiceVersion(),
                service.getServiceType(),
                200,
                Map.of(),
                responseBody
            );
        } catch (Exception ex) {
            throw new ServiceExecutionException(
                "DB execution failed: " + ex.getMessage(),
                ex,
                HttpStatusCode.valueOf(500),
                objectMapper.getNodeFactory().textNode(ex.getMessage())
            );
        }
    }

    private DriverManagerDataSource createDataSource(JsonNode connectionConfig) {
        String url = text(connectionConfig, "url");
        if (!StringUtils.hasText(url)) {
            throw new IllegalArgumentException("connection configJson.url is required for DB execution");
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(text(connectionConfig, "username"));
        dataSource.setPassword(text(connectionConfig, "password"));

        String driverClassName = firstNonBlank(
            text(connectionConfig, "driverClassName"),
            text(connectionConfig, "driver")
        );
        if (StringUtils.hasText(driverClassName)) {
            dataSource.setDriverClassName(driverClassName);
        }
        return dataSource;
    }

    private MapSqlParameterSource toSqlParameters(JsonNode payload) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (payload == null || payload.isNull()) {
            return params;
        }

        JsonNode source = payload.has("params") && payload.get("params").isObject()
            ? payload.get("params")
            : payload;

        if (!source.isObject()) {
            throw new IllegalArgumentException("DB payload must be a JSON object or contain object field 'params'");
        }

        source.fields().forEachRemaining(entry -> params.addValue(entry.getKey(), jsonNodeToValue(entry.getValue()), sqlTypeOf(entry.getValue())));
        return params;
    }

    private Object jsonNodeToValue(JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isTextual()) {
            return value.asText();
        }
        if (value.isIntegralNumber()) {
            return value.asLong();
        }
        if (value.isFloatingPointNumber()) {
            return value.asDouble();
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isArray() || value.isObject()) {
            return value.toString();
        }
        return value.asText();
    }

    private int sqlTypeOf(JsonNode value) {
        if (value == null || value.isNull()) {
            return Types.NULL;
        }
        if (value.isTextual()) {
            return Types.VARCHAR;
        }
        if (value.isIntegralNumber()) {
            return Types.BIGINT;
        }
        if (value.isFloatingPointNumber()) {
            return Types.DOUBLE;
        }
        if (value.isBoolean()) {
            return Types.BOOLEAN;
        }
        return Types.VARCHAR;
    }

    private JsonNode parseJson(String raw, String label) {
        try {
            return objectMapper.readTree(raw);
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " is not valid JSON");
        }
    }

    private String detectOperation(String sql) {
        String normalized = sql.trim().toUpperCase();
        if (normalized.startsWith("SELECT") || normalized.startsWith("WITH")) {
            return DbOperationType.QUERY.name();
        }
        if (normalized.startsWith("UPDATE")) {
            return DbOperationType.UPDATE.name();
        }
        if (normalized.startsWith("INSERT")) {
            return DbOperationType.INSERT.name();
        }
        if (normalized.startsWith("DELETE")) {
            return DbOperationType.DELETE.name();
        }
        return DbOperationType.QUERY.name();
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(fieldName);
        return value != null ? value.asText() : null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private DbOperationType resolveOperation(String operation) {
        if (!StringUtils.hasText(operation)) {
            return DbOperationType.QUERY;
        }
        try {
            return DbOperationType.valueOf(operation.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported DB operation: " + operation);
        }
    }
}
