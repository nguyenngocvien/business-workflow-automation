package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.RoleRepositoryPort;
import com.baw.identity.domain.model.Role;
import com.baw.identity.infrastructure.persistence.mapper.RolePersistenceMapper;
import com.baw.identity.infrastructure.persistence.repository.RoleRepository;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

	private final RoleRepository roleRepository;

	public RolePersistenceAdapter(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public Role save(Role role) {
		return RolePersistenceMapper.toDomain(roleRepository.save(RolePersistenceMapper.toEntity(role)));
	}

	@Override
	public Optional<Role> findById(Long id) {
		return roleRepository.findById(id).map(RolePersistenceMapper::toDomain);
	}

	@Override
	public Optional<Role> findByCode(String code) {
		return roleRepository.findByCode(code).map(RolePersistenceMapper::toDomain);
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll().stream().map(RolePersistenceMapper::toDomain).toList();
	}
}
