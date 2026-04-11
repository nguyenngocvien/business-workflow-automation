package com.connector.application.service.execution;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceExecutionException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final JsonNode responseBody;

    public ServiceExecutionException(String message, Throwable cause, HttpStatusCode statusCode, JsonNode responseBody) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public JsonNode getResponseBody() {
        return responseBody;
    }
}
