package com.baw.identity.infrastructure.persistence.adapter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.UserRoleRepositoryPort;
import com.baw.identity.domain.model.UserRoleAssignment;
import com.baw.identity.infrastructure.persistence.entity.RoleEntity;
import com.baw.identity.infrastructure.persistence.entity.UserEntity;
import com.baw.identity.infrastructure.persistence.entity.UserRoleEntity;
import com.baw.identity.infrastructure.persistence.entity.UserRoleId;
import com.baw.identity.infrastructure.persistence.repository.RoleRepository;
import com.baw.identity.infrastructure.persistence.repository.UserRepository;
import com.baw.identity.infrastructure.persistence.repository.UserRoleRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRolePersistenceAdapter implements UserRoleRepositoryPort {

	private final UserRoleRepository userRoleRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public UserRolePersistenceAdapter(
		UserRoleRepository userRoleRepository,
		UserRepository userRepository,
		RoleRepository roleRepository
	) {
		this.userRoleRepository = userRoleRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public UserRoleAssignment save(Long userId, Long roleId, OffsetDateTime expiresAt) {
		UserEntity user = userRepository.getReferenceById(userId);
		RoleEntity role = roleRepository.getReferenceById(roleId);

		UserRoleEntity entity = new UserRoleEntity();
		entity.setId(new UserRoleId(userId, roleId));
		entity.setUser(user);
		entity.setRole(role);
		entity.setExpiresAt(expiresAt);

		UserRoleEntity saved = userRoleRepository.save(entity);
		return toDomain(saved);
	}

	@Override
	public void delete(Long userId, Long roleId) {
		userRoleRepository.deleteById(new UserRoleId(userId, roleId));
	}

	@Override
	public boolean exists(Long userId, Long roleId) {
		return userRoleRepository.existsById(new UserRoleId(userId, roleId));
	}

	@Override
	public Optional<UserRoleAssignment> find(Long userId, Long roleId) {
		return userRoleRepository.findById(new UserRoleId(userId, roleId)).map(this::toDomain);
	}

	@Override
	public List<UserRoleAssignment> findByUserId(Long userId) {
		return userRoleRepository.findAll().stream()
			.filter(entity -> entity.getUser() != null && entity.getUser().getId().equals(userId))
			.map(this::toDomain)
			.toList();
	}

	private UserRoleAssignment toDomain(UserRoleEntity entity) {
		return new UserRoleAssignment(
			entity.getUser() != null ? entity.getUser().getId() : null,
			entity.getRole() != null ? entity.getRole().getId() : null,
			entity.getAssignedAt(),
			entity.getExpiresAt()
		);
	}
}
