package com.baw.identity.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record GrantRolePermissionRequest(
	@NotNull
	Long roleId,
	@NotNull
	Long permissionId
) {
}
