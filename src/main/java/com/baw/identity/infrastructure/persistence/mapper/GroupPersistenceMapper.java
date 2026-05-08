package com.baw.identity.infrastructure.persistence.mapper;

import com.baw.identity.domain.model.Group;
import com.baw.identity.infrastructure.persistence.entity.GroupEntity;

public final class GroupPersistenceMapper {

	private GroupPersistenceMapper() {
	}

	public static Group toDomain(GroupEntity entity) {
		if (entity == null) {
			return null;
		}

		return new Group(
			entity.getId(),
			entity.getCode(),
			entity.getName(),
			entity.getParentGroup() != null ? entity.getParentGroup().getId() : null,
			entity.getGroupType(),
			entity.getPath(),
			entity.getDescription(),
			entity.getSource(),
			Boolean.TRUE.equals(entity.getIsActive()),
			entity.getCreatedAt()
		);
	}

	public static GroupEntity toEntity(Group domain) {
		if (domain == null) {
			return null;
		}

		GroupEntity entity = new GroupEntity();
		entity.setId(domain.id());
		entity.setCode(domain.code());
		entity.setName(domain.name());
		entity.setGroupType(domain.groupType());
		entity.setPath(domain.path());
		entity.setDescription(domain.description());
		entity.setSource(domain.source());
		entity.setIsActive(domain.active());
		return entity;
	}
}
