package com.baw.identity.application.port.in;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.UserRoleAssignment;

public interface UserRoleUseCase {

	UserRoleAssignment assign(AssignUserRoleCommand command);

	void revoke(Long userId, Long roleId);

	Optional<UserRoleAssignment> find(Long userId, Long roleId);

	List<UserRoleAssignment> findByUserId(Long userId);

	record AssignUserRoleCommand(
		Long userId,
		Long roleId,
		OffsetDateTime expiresAt
	) {
	}
}
