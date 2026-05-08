package com.connector.api.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "Standard error response returned by the API")
public record ApiErrorResponse(
    @Schema(description = "Timestamp when the error occurred", example = "2026-04-19T10:15:30")
    LocalDateTime timestamp,
    @Schema(description = "HTTP status code", example = "400")
    int status,
    @Schema(description = "HTTP reason phrase", example = "Bad Request")
    String error,
    @Schema(description = "Human-readable error message", example = "id must not be null")
    String message
) {
}
