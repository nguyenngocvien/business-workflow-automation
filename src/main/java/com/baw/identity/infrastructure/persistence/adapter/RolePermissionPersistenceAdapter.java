package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.RolePermissionRepositoryPort;
import com.baw.identity.domain.model.RolePermissionGrant;
import com.baw.identity.infrastructure.persistence.entity.PermissionEntity;
import com.baw.identity.infrastructure.persistence.entity.RoleEntity;
import com.baw.identity.infrastructure.persistence.entity.RolePermissionEntity;
import com.baw.identity.infrastructure.persistence.entity.RolePermissionId;
import com.baw.identity.infrastructure.persistence.repository.PermissionRepository;
import com.baw.identity.infrastructure.persistence.repository.RolePermissionRepository;
import com.baw.identity.infrastructure.persistence.repository.RoleRepository;
import org.springframework.stereotype.Component;

@Component
public class RolePermissionPersistenceAdapter implements RolePermissionRepositoryPort {

	private final RolePermissionRepository rolePermissionRepository;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public RolePermissionPersistenceAdapter(
		RolePermissionRepository rolePermissionRepository,
		RoleRepository roleRepository,
		PermissionRepository permissionRepository
	) {
		this.rolePermissionRepository = rolePermissionRepository;
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
	}

	@Override
	public RolePermissionGrant save(Long roleId, Long permissionId) {
		RoleEntity role = roleRepository.getReferenceById(roleId);
		PermissionEntity permission = permissionRepository.getReferenceById(permissionId);

		RolePermissionEntity entity = new RolePermissionEntity();
		entity.setId(new RolePermissionId(roleId, permissionId));
		entity.setRole(role);
		entity.setPermission(permission);

		return toDomain(rolePermissionRepository.save(entity));
	}

	@Override
	public void delete(Long roleId, Long permissionId) {
		rolePermissionRepository.deleteById(new RolePermissionId(roleId, permissionId));
	}

	@Override
	public boolean exists(Long roleId, Long permissionId) {
		return rolePermissionRepository.existsById(new RolePermissionId(roleId, permissionId));
	}

	@Override
	public Optional<RolePermissionGrant> find(Long roleId, Long permissionId) {
		return rolePermissionRepository.findById(new RolePermissionId(roleId, permissionId)).map(this::toDomain);
	}

	@Override
	public List<RolePermissionGrant> findByRoleId(Long roleId) {
		return rolePermissionRepository.findAll().stream()
			.filter(entity -> entity.getRole() != null && entity.getRole().getId().equals(roleId))
			.map(this::toDomain)
			.toList();
	}

	private RolePermissionGrant toDomain(RolePermissionEntity entity) {
		return new RolePermissionGrant(
			entity.getRole() != null ? entity.getRole().getId() : null,
			entity.getPermission() != null ? entity.getPermission().getId() : null
		);
	}
}
