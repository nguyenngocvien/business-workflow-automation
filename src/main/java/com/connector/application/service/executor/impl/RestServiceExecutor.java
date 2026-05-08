package com.connector.application.service.executor.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.port.out.HttpClient;
import com.connector.application.port.out.model.HttpRequest;
import com.connector.application.port.out.model.HttpResponse;
import com.connector.application.service.executor.AbstractServiceExecutor;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestServiceExecutor extends AbstractServiceExecutor {

    private final HttpClient httpClient;

    public RestServiceExecutor(ObjectMapper mapper, HttpClient httpClient) {
        super(mapper);
        this.httpClient = httpClient;
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.REST;
    }

    @Override
    protected Object prepare(ServiceEntity service, JsonNode config, ExecuteServiceCommand request) {

        HttpRequest r = new HttpRequest();
        r.setUrl(text(config, "url"));
        r.setMethod(text(config, "method"));

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");

        r.setHeaders(headers);
        r.setBody(toJson(request.payload()));

        return r;
    }

    @Override
    protected Object invoke(Object prepared) {
        return httpClient.execute((HttpRequest) prepared);
    }

    @Override
    protected JsonNode buildBody(Object result) {
        HttpResponse r = (HttpResponse) result;
        return parseJsonSafe(r.getBody());
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode parseJsonSafe(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            return objectMapper.getNodeFactory().textNode(body);
        }
    }
}