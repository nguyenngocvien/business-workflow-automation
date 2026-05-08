package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.in.GroupUseCase;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.out.GroupRepositoryPort;
import com.baw.identity.domain.model.Group;
import com.baw.identity.domain.model.enums.RecordSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupService implements GroupUseCase {

	private final GroupRepositoryPort groupRepositoryPort;

	public GroupService(GroupRepositoryPort groupRepositoryPort) {
		this.groupRepositoryPort = groupRepositoryPort;
	}

	@Override
	public Group create(CreateGroupCommand command) {
		Group group = new Group(
			null,
			command.code(),
			command.name(),
			command.parentGroupId(),
			command.groupType(),
			command.path(),
			command.description(),
			command.source() == null ? RecordSource.LOCAL : command.source(),
			true,
			null
		);
		return groupRepositoryPort.save(group);
	}

	@Override
	public Group update(Long id, UpdateGroupCommand command) {
		Group existing = groupRepositoryPort.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Group not found: " + id));

		Group updated = new Group(
			existing.id(),
			command.code() != null ? command.code() : existing.code(),
			command.name() != null ? command.name() : existing.name(),
			command.parentGroupId() != null ? command.parentGroupId() : existing.parentGroupId(),
			command.groupType() != null ? command.groupType() : existing.groupType(),
			command.path() != null ? command.path() : existing.path(),
			command.description() != null ? command.description() : existing.description(),
			command.source() != null ? command.source() : existing.source(),
			existing.active(),
			existing.createdAt()
		);
		return groupRepositoryPort.save(updated);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Group> findById(Long id) {
		return groupRepositoryPort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Group> findByCode(String code) {
		return groupRepositoryPort.findByCode(code);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Group> findAll() {
		return groupRepositoryPort.findAll();
	}
}
