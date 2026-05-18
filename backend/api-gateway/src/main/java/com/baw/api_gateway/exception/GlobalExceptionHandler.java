package com.baw.api_gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ResponseStatusException.class,
            HttpClientErrorException.class,
            HttpServerErrorException.class
    })
    public ResponseEntity<ApiErrorResponse> handleUpstreamFailure(Exception ex, ServerWebExchange exchange) {
        HttpStatus status = extractStatus(ex);
        return buildErrorResponse(status, status.getReasonPhrase(), safeMessage(ex), exchange);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, WebExchangeBindException.class})
    public ResponseEntity<ApiErrorResponse> handleValidation(Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Request validation failed", exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", safeMessage(ex), exchange);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            ServerWebExchange exchange
    ) {
        ApiErrorResponse body = JsonErrorResponseFactory.create(status, error, message, exchange.getRequest().getPath().value());
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
