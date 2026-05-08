package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.in.RoleUseCase;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.out.RoleRepositoryPort;
import com.baw.identity.domain.model.Role;
import com.baw.identity.domain.model.enums.RecordSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService implements RoleUseCase {

	private final RoleRepositoryPort roleRepositoryPort;

	public RoleService(RoleRepositoryPort roleRepositoryPort) {
		this.roleRepositoryPort = roleRepositoryPort;
	}

	@Override
	public Role create(CreateRoleCommand command) {
		Role role = new Role(
			null,
			command.code(),
			command.name(),
			command.roleType(),
			command.description(),
			command.source() == null ? RecordSource.LOCAL : command.source(),
			Boolean.TRUE.equals(command.system()),
			null
		);
		return roleRepositoryPort.save(role);
	}

	@Override
	public Role update(Long id, UpdateRoleCommand command) {
		Role existing = roleRepositoryPort.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));

		Role updated = new Role(
			existing.id(),
			command.code() != null ? command.code() : existing.code(),
			command.name() != null ? command.name() : existing.name(),
			command.roleType() != null ? command.roleType() : existing.roleType(),
			command.description() != null ? command.description() : existing.description(),
			command.source() != null ? command.source() : existing.source(),
			command.system() != null ? command.system() : existing.system(),
			existing.createdAt()
		);
		return roleRepositoryPort.save(updated);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Role> findById(Long id) {
		return roleRepositoryPort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Role> findByCode(String code) {
		return roleRepositoryPort.findByCode(code);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Role> findAll() {
		return roleRepositoryPort.findAll();
	}
}
