package com.baw.identity.api.dto.request;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;

public record AssignUserRoleRequest(
	@NotNull
	Long userId,
	@NotNull
	Long roleId,
	OffsetDateTime expiresAt
) {
}
