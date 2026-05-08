package com.baw.identity.application.port.in;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.UserGroupMembership;

public interface UserGroupUseCase {

	UserGroupMembership add(AddUserGroupCommand command);

	void remove(Long userId, Long groupId);

	Optional<UserGroupMembership> find(Long userId, Long groupId);

	List<UserGroupMembership> findByUserId(Long userId);

	record AddUserGroupCommand(
		Long userId,
		Long groupId
	) {
	}
}
