package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;

import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.in.UserRoleUseCase;
import com.baw.identity.application.port.out.RoleRepositoryPort;
import com.baw.identity.application.port.out.UserRepositoryPort;
import com.baw.identity.application.port.out.UserRoleRepositoryPort;
import com.baw.identity.domain.model.UserRoleAssignment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserRoleService implements UserRoleUseCase {

	private final UserRepositoryPort userRepositoryPort;
	private final RoleRepositoryPort roleRepositoryPort;
	private final UserRoleRepositoryPort userRoleRepositoryPort;

	public UserRoleService(
		UserRepositoryPort userRepositoryPort,
		RoleRepositoryPort roleRepositoryPort,
		UserRoleRepositoryPort userRoleRepositoryPort
	) {
		this.userRepositoryPort = userRepositoryPort;
		this.roleRepositoryPort = roleRepositoryPort;
		this.userRoleRepositoryPort = userRoleRepositoryPort;
	}

	@Override
	public UserRoleAssignment assign(AssignUserRoleCommand command) {
		userRepositoryPort.findById(command.userId())
			.orElseThrow(() -> new ResourceNotFoundException("User not found: " + command.userId()));
		roleRepositoryPort.findById(command.roleId())
			.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + command.roleId()));
		return userRoleRepositoryPort.save(command.userId(), command.roleId(), command.expiresAt());
	}

	@Override
	public void revoke(Long userId, Long roleId) {
		userRoleRepositoryPort.delete(userId, roleId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserRoleAssignment> find(Long userId, Long roleId) {
		return userRoleRepositoryPort.find(userId, roleId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserRoleAssignment> findByUserId(Long userId) {
		return userRoleRepositoryPort.findByUserId(userId);
	}
}
