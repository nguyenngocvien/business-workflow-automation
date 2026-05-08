package com.baw.identity.api.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Machine-readable error code for client-side handling")
public enum ErrorCode {
	RESOURCE_NOT_FOUND,
	VALIDATION_FAILED,
	BAD_REQUEST,
	INTERNAL_ERROR
}
