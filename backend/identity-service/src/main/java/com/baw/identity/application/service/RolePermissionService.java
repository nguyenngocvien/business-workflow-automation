package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;

import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.in.RolePermissionUseCase;
import com.baw.identity.application.port.out.PermissionRepositoryPort;
import com.baw.identity.application.port.out.RolePermissionRepositoryPort;
import com.baw.identity.application.port.out.RoleRepositoryPort;
import com.baw.identity.domain.model.RolePermissionGrant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RolePermissionService implements RolePermissionUseCase {

	private final RoleRepositoryPort roleRepositoryPort;
	private final PermissionRepositoryPort permissionRepositoryPort;
	private final RolePermissionRepositoryPort rolePermissionRepositoryPort;

	public RolePermissionService(
		RoleRepositoryPort roleRepositoryPort,
		PermissionRepositoryPort permissionRepositoryPort,
		RolePermissionRepositoryPort rolePermissionRepositoryPort
	) {
		this.roleRepositoryPort = roleRepositoryPort;
		this.permissionRepositoryPort = permissionRepositoryPort;
		this.rolePermissionRepositoryPort = rolePermissionRepositoryPort;
	}

	@Override
	public RolePermissionGrant grant(GrantRolePermissionCommand command) {
		roleRepositoryPort.findById(command.roleId())
			.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + command.roleId()));
		permissionRepositoryPort.findById(command.permissionId())
			.orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + command.permissionId()));
		return rolePermissionRepositoryPort.save(command.roleId(), command.permissionId());
	}

	@Override
	public void revoke(Long roleId, Long permissionId) {
		rolePermissionRepositoryPort.delete(roleId, permissionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RolePermissionGrant> find(Long roleId, Long permissionId) {
		return rolePermissionRepositoryPort.find(roleId, permissionId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RolePermissionGrant> findByRoleId(Long roleId) {
		return rolePermissionRepositoryPort.findByRoleId(roleId);
	}
}
