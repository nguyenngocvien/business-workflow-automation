package com.baw.api_gateway.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;

public final class JsonErrorResponseFactory {

    private JsonErrorResponseFactory() {
    }

    public static ApiErrorResponse create(HttpStatus status, String error, String message, String path) {
        String resolvedMessage = (message == null || message.isBlank()) ? error : message;
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                error,
                resolvedMessage,
                path == null ? "" : path
        );
    }
}
