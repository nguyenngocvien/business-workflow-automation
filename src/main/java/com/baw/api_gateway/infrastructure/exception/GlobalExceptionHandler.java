package com.baw.api_gateway.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ResponseStatusException.class,
            HttpClientErrorException.class,
            HttpServerErrorException.class
    })
    public ResponseEntity<ApiErrorResponse> handleUpstreamFailure(Exception ex, HttpServletRequest request) {
        HttpStatus status = extractStatus(ex);
        return buildErrorResponse(status, status.getReasonPhrase(), safeMessage(ex), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Request validation failed", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", safeMessage(ex), request);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = JsonErrorResponseFactory.create(status, error, message, request);
        return ResponseEntity.status(status).body(body);
    }

    private static HttpStatus extractStatus(Exception ex) {
    if (ex instanceof ResponseStatusException responseStatusException) {
        return HttpStatus.valueOf(responseStatusException.getStatusCode().value());
    }

    if (ex instanceof HttpClientErrorException httpClientErrorException) {
        return HttpStatus.valueOf(httpClientErrorException.getStatusCode().value());
    }

    if (ex instanceof HttpServerErrorException httpServerErrorException) {
        return HttpStatus.valueOf(httpServerErrorException.getStatusCode().value());
    }

    return HttpStatus.BAD_GATEWAY;
}

    private static String safeMessage(Exception ex) {
        String message = ex.getMessage();
        return (message == null || message.isBlank()) ? "Unexpected error" : message;
    }
}
