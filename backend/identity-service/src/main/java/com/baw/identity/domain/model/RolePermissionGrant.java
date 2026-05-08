package com.baw.identity.domain.model;

public record RolePermissionGrant(
	Long roleId,
	Long permissionId
) {
}
