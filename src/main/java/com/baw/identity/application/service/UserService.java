package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.baw.identity.application.port.in.UserUseCase;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.out.UserRepositoryPort;
import com.baw.identity.domain.model.User;
import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements UserUseCase {

	private final UserRepositoryPort userRepositoryPort;

	public UserService(UserRepositoryPort userRepositoryPort) {
		this.userRepositoryPort = userRepositoryPort;
	}

	@Override
	public User create(CreateUserCommand command) {
		User user = new User(
			null,
			command.externalId(),
			command.username(),
			command.email(),
			command.firstName(),
			command.lastName(),
			command.fullName(),
			command.phoneNumber(),
			command.status() == null ? UserStatus.ACTIVE : command.status(),
			command.source() == null ? RecordSource.LOCAL : command.source(),
			false,
			null,
			null
		);
		return userRepositoryPort.save(user);
	}

	@Override
	public User update(Long id, UpdateUserCommand command) {
		User existing = userRepositoryPort.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

		User updated = new User(
			existing.id(),
			command.externalId() != null ? command.externalId() : existing.externalId(),
			command.username() != null ? command.username() : existing.username(),
			command.email() != null ? command.email() : existing.email(),
			command.firstName() != null ? command.firstName() : existing.firstName(),
			command.lastName() != null ? command.lastName() : existing.lastName(),
			command.fullName() != null ? command.fullName() : existing.fullName(),
			command.phoneNumber() != null ? command.phoneNumber() : existing.phoneNumber(),
			command.status() != null ? command.status() : existing.status(),
			command.source() != null ? command.source() : existing.source(),
			existing.deleted(),
			existing.createdAt(),
			existing.updatedAt()
		);
		return userRepositoryPort.save(updated);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findById(Long id) {
		return userRepositoryPort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByUsername(String username) {
		return userRepositoryPort.findByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByExternalId(UUID externalId) {
		return userRepositoryPort.findByExternalId(externalId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return userRepositoryPort.findAll();
	}

	@Override
	public User deactivate(Long id) {
		User existing = userRepositoryPort.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

		User deactivated = new User(
			existing.id(),
			existing.externalId(),
			existing.username(),
			existing.email(),
			existing.firstName(),
			existing.lastName(),
			existing.fullName(),
			existing.phoneNumber(),
			UserStatus.INACTIVE,
			existing.source(),
			true,
			existing.createdAt(),
			existing.updatedAt()
		);
		return userRepositoryPort.save(deactivated);
	}
}
