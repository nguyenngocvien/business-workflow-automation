package com.baw.identity.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddUserGroupRequest(
	@NotNull
	Long userId,
	@NotNull
	Long groupId
) {
}
