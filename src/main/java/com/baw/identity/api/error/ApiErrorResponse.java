package com.baw.identity.api.error;

import java.time.OffsetDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "Standard error payload returned by the API")
public record ApiErrorResponse(
	@Schema(description = "Timestamp when the error occurred")
	OffsetDateTime timestamp,
	@Schema(description = "Machine-readable error code")
	ErrorCode errorCode,
	@Schema(description = "HTTP status code")
	int status,
	@Schema(description = "HTTP reason phrase")
	String error,
	@Schema(description = "Human-readable error message")
	String message,
	@Schema(description = "Request path that produced the error")
	String path,
	@Schema(description = "Field-level validation violations, if any")
	List<FieldViolation> violations
) {
	@Schema(name = "FieldViolation", description = "A single validation error for one field")
	public record FieldViolation(String field, String message) {
	}
}
