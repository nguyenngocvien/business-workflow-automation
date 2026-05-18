package com.connector.application.exception;

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

    public ServiceExecutionException(String string, Exception ex, HttpStatusCode valueOf) {
        super(string, ex);
        this.statusCode = valueOf;
        this.responseBody = null;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public JsonNode getResponseBody() {
        return responseBody;
    }
}
