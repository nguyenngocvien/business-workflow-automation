package com.baw.identity.domain.model;

import java.time.OffsetDateTime;

public record UserGroupMembership(
	Long userId,
	Long groupId,
	OffsetDateTime joinedAt
) {
}
