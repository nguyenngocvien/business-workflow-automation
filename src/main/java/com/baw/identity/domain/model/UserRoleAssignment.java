package com.baw.identity.domain.model;

import java.time.OffsetDateTime;

public record UserRoleAssignment(
	Long userId,
	Long roleId,
	OffsetDateTime assignedAt,
	OffsetDateTime expiresAt
) {
}
