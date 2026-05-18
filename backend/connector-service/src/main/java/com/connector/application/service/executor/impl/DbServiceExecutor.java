package com.connector.application.service.executor.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.port.out.DatabaseClient;
import com.connector.application.port.out.model.DbRequest;
import com.connector.application.service.executor.AbstractServiceExecutor;
import com.connector.domain.entity.ConnectionEntity;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DbServiceExecutor extends AbstractServiceExecutor {

    private final DatabaseClient databaseClient;

    public DbServiceExecutor(ObjectMapper mapper, DatabaseClient databaseClient) {
        super(mapper);
        this.databaseClient = databaseClient;
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.DB;
    }

    @Override
    protected Object prepare(ServiceEntity service, JsonNode config, ExecuteServiceCommand request) {

        ConnectionEntity conn = service.getConnection();

        DbRequest r = new DbRequest();
        r.setUrl(text(connConfig(conn), "url"));
        r.setUsername(text(connConfig(conn), "username"));
        r.setPassword(text(connConfig(conn), "password"));
        r.setSql(text(config, "sql"));
        r.setParams(
                objectMapper.convertValue(
                        request.payload(),
                        new TypeReference<Map<String, Object>>() {
                        }));

        return r;
    }

    @Override
    protected Object invoke(Object prepared) {
        DbRequest r = (DbRequest) prepared;

        if (r.getSql().trim().toUpperCase().startsWith("SELECT")) {
            return databaseClient.executeQuery(r);
        }
        return databaseClient.executeUpdate(r);
    }

    @Override
    protected JsonNode buildBody(Object result) {

        if (result instanceof Integer affected) {
            return objectMapper.createObjectNode()
                    .put("affectedRows", affected);
        }

        return objectMapper.createObjectNode()
                .set("rows", objectMapper.valueToTree(result));
    }

    private JsonNode connConfig(ConnectionEntity c) {
        return parseJson(c.getConfigJson());
    }
}