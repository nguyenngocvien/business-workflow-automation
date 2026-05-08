package com.baw.identity.application.port.in;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.RolePermissionGrant;

public interface RolePermissionUseCase {

	RolePermissionGrant grant(GrantRolePermissionCommand command);

	void revoke(Long roleId, Long permissionId);

	Optional<RolePermissionGrant> find(Long roleId, Long permissionId);

	List<RolePermissionGrant> findByRoleId(Long roleId);

	record GrantRolePermissionCommand(
		Long roleId,
		Long permissionId
	) {
	}
}
