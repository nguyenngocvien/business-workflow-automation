package com.baw.identity.application.port.in;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.Group;
import com.baw.identity.domain.model.enums.GroupType;
import com.baw.identity.domain.model.enums.RecordSource;

public interface GroupUseCase {

	Group create(CreateGroupCommand command);

	Group update(Long id, UpdateGroupCommand command);

	Optional<Group> findById(Long id);

	Optional<Group> findByCode(String code);

	List<Group> findAll();

	record CreateGroupCommand(
		String code,
		String name,
		Long parentGroupId,
		GroupType groupType,
		String path,
		String description,
		RecordSource source
	) {
	}

	record UpdateGroupCommand(
		String code,
		String name,
		Long parentGroupId,
		GroupType groupType,
		String path,
		String description,
		RecordSource source
	) {
	}
}
