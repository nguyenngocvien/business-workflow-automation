package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.UserGroupRepositoryPort;
import com.baw.identity.domain.model.UserGroupMembership;
import com.baw.identity.infrastructure.persistence.entity.GroupEntity;
import com.baw.identity.infrastructure.persistence.entity.UserEntity;
import com.baw.identity.infrastructure.persistence.entity.UserGroupEntity;
import com.baw.identity.infrastructure.persistence.entity.UserGroupId;
import com.baw.identity.infrastructure.persistence.repository.GroupRepository;
import com.baw.identity.infrastructure.persistence.repository.UserGroupRepository;
import com.baw.identity.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserGroupPersistenceAdapter implements UserGroupRepositoryPort {

	private final UserGroupRepository userGroupRepository;
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;

	public UserGroupPersistenceAdapter(
		UserGroupRepository userGroupRepository,
		UserRepository userRepository,
		GroupRepository groupRepository
	) {
		this.userGroupRepository = userGroupRepository;
		this.userRepository = userRepository;
		this.groupRepository = groupRepository;
	}

	@Override
	public UserGroupMembership save(Long userId, Long groupId) {
		UserEntity user = userRepository.getReferenceById(userId);
		GroupEntity group = groupRepository.getReferenceById(groupId);

		UserGroupEntity entity = new UserGroupEntity();
		entity.setId(new UserGroupId(userId, groupId));
		entity.setUser(user);
		entity.setGroup(group);

		return toDomain(userGroupRepository.save(entity));
	}

	@Override
	public void delete(Long userId, Long groupId) {
		userGroupRepository.deleteById(new UserGroupId(userId, groupId));
	}

	@Override
	public boolean exists(Long userId, Long groupId) {
		return userGroupRepository.existsById(new UserGroupId(userId, groupId));
	}

	@Override
	public Optional<UserGroupMembership> find(Long userId, Long groupId) {
		return userGroupRepository.findById(new UserGroupId(userId, groupId)).map(this::toDomain);
	}

	@Override
	public List<UserGroupMembership> findByUserId(Long userId) {
		return userGroupRepository.findAll().stream()
			.filter(entity -> entity.getUser() != null && entity.getUser().getId().equals(userId))
			.map(this::toDomain)
			.toList();
	}

	private UserGroupMembership toDomain(UserGroupEntity entity) {
		return new UserGroupMembership(
			entity.getUser() != null ? entity.getUser().getId() : null,
			entity.getGroup() != null ? entity.getGroup().getId() : null,
			entity.getJoinedAt()
		);
	}
}
