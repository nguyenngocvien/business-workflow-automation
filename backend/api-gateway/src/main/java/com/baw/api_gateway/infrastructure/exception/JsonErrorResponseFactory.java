package com.baw.api_gateway.infrastructure.exception;

import java.time.OffsetDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

public final class JsonErrorResponseFactory {

    private JsonErrorResponseFactory() {
    }

    public static ApiErrorResponse create(HttpStatus status, String error, String message, HttpServletRequest request) {
        String resolvedMessage = (message == null || message.isBlank()) ? error : message;
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                error,
                resolvedMessage,
                request.getRequestURI()
        );
    }
}
