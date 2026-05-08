package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.GroupRepositoryPort;
import com.baw.identity.domain.model.Group;
import com.baw.identity.infrastructure.persistence.mapper.GroupPersistenceMapper;
import com.baw.identity.infrastructure.persistence.repository.GroupRepository;
import org.springframework.stereotype.Component;

@Component
public class GroupPersistenceAdapter implements GroupRepositoryPort {

	private final GroupRepository groupRepository;

	public GroupPersistenceAdapter(GroupRepository groupRepository) {
		this.groupRepository = groupRepository;
	}

	@Override
	public Group save(Group group) {
		var entity = GroupPersistenceMapper.toEntity(group);
		if (group.parentGroupId() != null) {
			entity.setParentGroup(groupRepository.getReferenceById(group.parentGroupId()));
		}
		return GroupPersistenceMapper.toDomain(groupRepository.save(entity));
	}

	@Override
	public Optional<Group> findById(Long id) {
		return groupRepository.findById(id).map(GroupPersistenceMapper::toDomain);
	}

	@Override
	public Optional<Group> findByCode(String code) {
		return groupRepository.findByCode(code).map(GroupPersistenceMapper::toDomain);
	}

	@Override
	public List<Group> findAll() {
		return groupRepository.findAll().stream().map(GroupPersistenceMapper::toDomain).toList();
	}
}
