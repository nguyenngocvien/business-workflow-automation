package com.baw.identity.api.dto.response;

import java.time.OffsetDateTime;

public record UserGroupResponse(
	Long userId,
	Long groupId,
	OffsetDateTime joinedAt
) {
}
