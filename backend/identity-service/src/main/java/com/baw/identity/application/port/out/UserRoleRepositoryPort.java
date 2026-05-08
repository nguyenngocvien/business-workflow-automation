package com.baw.identity.application.port.out;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.UserRoleAssignment;

public interface UserRoleRepositoryPort {

	UserRoleAssignment save(Long userId, Long roleId, OffsetDateTime expiresAt);

	void delete(Long userId, Long roleId);

	boolean exists(Long userId, Long roleId);

	Optional<UserRoleAssignment> find(Long userId, Long roleId);

	List<UserRoleAssignment> findByUserId(Long userId);
}
