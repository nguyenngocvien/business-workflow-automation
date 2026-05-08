package com.baw.identity.api.error;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
		return build(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
		List<ApiErrorResponse.FieldViolation> violations = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(this::toViolation)
			.toList();
		return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "Validation failed", request, violations);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
		return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, WebRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "An unexpected error occurred", request, List.of());
	}

	private ApiErrorResponse.FieldViolation toViolation(FieldError error) {
		return new ApiErrorResponse.FieldViolation(error.getField(), error.getDefaultMessage());
	}

	private ResponseEntity<ApiErrorResponse> build(
		HttpStatus status,
		ErrorCode errorCode,
		String message,
		WebRequest request,
		List<ApiErrorResponse.FieldViolation> violations
	) {
		String path = request.getDescription(false);
		if (path != null && path.startsWith("uri=")) {
			path = path.substring(4);
		}

		ApiErrorResponse body = new ApiErrorResponse(
			OffsetDateTime.now(),
			errorCode,
			status.value(),
			status.getReasonPhrase(),
			message,
			path,
			violations
		);
		return ResponseEntity.status(status).body(body);
	}
}
