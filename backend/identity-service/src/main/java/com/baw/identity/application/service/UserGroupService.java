package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;

import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.in.UserGroupUseCase;
import com.baw.identity.application.port.out.GroupRepositoryPort;
import com.baw.identity.application.port.out.UserGroupRepositoryPort;
import com.baw.identity.application.port.out.UserRepositoryPort;
import com.baw.identity.domain.model.UserGroupMembership;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserGroupService implements UserGroupUseCase {

	private final UserRepositoryPort userRepositoryPort;
	private final GroupRepositoryPort groupRepositoryPort;
	private final UserGroupRepositoryPort userGroupRepositoryPort;

	public UserGroupService(
		UserRepositoryPort userRepositoryPort,
		GroupRepositoryPort groupRepositoryPort,
		UserGroupRepositoryPort userGroupRepositoryPort
	) {
		this.userRepositoryPort = userRepositoryPort;
		this.groupRepositoryPort = groupRepositoryPort;
		this.userGroupRepositoryPort = userGroupRepositoryPort;
	}

	@Override
	public UserGroupMembership add(AddUserGroupCommand command) {
		userRepositoryPort.findById(command.userId())
			.orElseThrow(() -> new ResourceNotFoundException("User not found: " + command.userId()));
		groupRepositoryPort.findById(command.groupId())
			.orElseThrow(() -> new ResourceNotFoundException("Group not found: " + command.groupId()));
		return userGroupRepositoryPort.save(command.userId(), command.groupId());
	}

	@Override
	public void remove(Long userId, Long groupId) {
		userGroupRepositoryPort.delete(userId, groupId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserGroupMembership> find(Long userId, Long groupId) {
		return userGroupRepositoryPort.find(userId, groupId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserGroupMembership> findByUserId(Long userId) {
		return userGroupRepositoryPort.findByUserId(userId);
	}
}
