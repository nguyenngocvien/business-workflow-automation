package com.baw.identity.application.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.baw.identity.domain.model.User;
import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.UserStatus;

public interface UserUseCase {

	User create(CreateUserCommand command);

	User update(Long id, UpdateUserCommand command);

	Optional<User> findById(Long id);

	Optional<User> findByUsername(String username);

	Optional<User> findByExternalId(UUID externalId);

	List<User> findAll();

	User deactivate(Long id);

	record CreateUserCommand(
		UUID externalId,
		String username,
		String email,
		String firstName,
		String lastName,
		String fullName,
		String phoneNumber,
		UserStatus status,
		RecordSource source
	) {
	}

	record UpdateUserCommand(
		UUID externalId,
		String username,
		String email,
		String firstName,
		String lastName,
		String fullName,
		String phoneNumber,
		UserStatus status,
		RecordSource source
	) {
	}
}
