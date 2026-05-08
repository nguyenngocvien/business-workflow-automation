package com.baw.identity.infrastructure.persistence.mapper;

import com.baw.identity.domain.model.Role;
import com.baw.identity.infrastructure.persistence.entity.RoleEntity;

public final class RolePersistenceMapper {

	private RolePersistenceMapper() {
	}

	public static Role toDomain(RoleEntity entity) {
		if (entity == null) {
			return null;
		}

		return new Role(
			entity.getId(),
			entity.getCode(),
			entity.getName(),
			entity.getRoleType(),
			entity.getDescription(),
			entity.getSource(),
			Boolean.TRUE.equals(entity.getIsSystem()),
			entity.getCreatedAt()
		);
	}

	public static RoleEntity toEntity(Role domain) {
		if (domain == null) {
			return null;
		}

		RoleEntity entity = new RoleEntity();
		entity.setId(domain.id());
		entity.setCode(domain.code());
		entity.setName(domain.name());
		entity.setRoleType(domain.roleType());
		entity.setDescription(domain.description());
		entity.setSource(domain.source());
		entity.setIsSystem(domain.system());
		return entity;
	}
}
