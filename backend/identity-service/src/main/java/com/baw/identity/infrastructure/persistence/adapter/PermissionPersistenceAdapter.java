package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.PermissionRepositoryPort;
import com.baw.identity.domain.model.Permission;
import com.baw.identity.infrastructure.persistence.mapper.PermissionPersistenceMapper;
import com.baw.identity.infrastructure.persistence.repository.PermissionRepository;
import org.springframework.stereotype.Component;

@Component
public class PermissionPersistenceAdapter implements PermissionRepositoryPort {

	private final PermissionRepository permissionRepository;

	public PermissionPersistenceAdapter(PermissionRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}

	@Override
	public Permission save(Permission permission) {
		return PermissionPersistenceMapper.toDomain(permissionRepository.save(PermissionPersistenceMapper.toEntity(permission)));
	}

	@Override
	public Optional<Permission> findById(Long id) {
		return permissionRepository.findById(id).map(PermissionPersistenceMapper::toDomain);
	}

	@Override
	public Optional<Permission> findByCode(String code) {
		return permissionRepository.findByCode(code).map(PermissionPersistenceMapper::toDomain);
	}

	@Override
	public List<Permission> findAll() {
		return permissionRepository.findAll().stream().map(PermissionPersistenceMapper::toDomain).toList();
	}
}
