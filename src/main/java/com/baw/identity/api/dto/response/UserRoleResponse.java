package com.baw.identity.api.dto.response;

import java.time.OffsetDateTime;

public record UserRoleResponse(
	Long userId,
	Long roleId,
	OffsetDateTime assignedAt,
	OffsetDateTime expiresAt
) {
}
