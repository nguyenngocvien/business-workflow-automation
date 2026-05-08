package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.UserGroupMembership;

public interface UserGroupRepositoryPort {

	UserGroupMembership save(Long userId, Long groupId);

	void delete(Long userId, Long groupId);

	boolean exists(Long userId, Long groupId);

	Optional<UserGroupMembership> find(Long userId, Long groupId);

	List<UserGroupMembership> findByUserId(Long userId);
}
