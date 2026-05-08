package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.RolePermissionGrant;

public interface RolePermissionRepositoryPort {

	RolePermissionGrant save(Long roleId, Long permissionId);

	void delete(Long roleId, Long permissionId);

	boolean exists(Long roleId, Long permissionId);

	Optional<RolePermissionGrant> find(Long roleId, Long permissionId);

	List<RolePermissionGrant> findByRoleId(Long roleId);
}
