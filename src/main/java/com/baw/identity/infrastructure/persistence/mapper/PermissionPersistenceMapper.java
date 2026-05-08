package com.baw.identity.infrastructure.persistence.mapper;

import com.baw.identity.domain.model.Permission;
import com.baw.identity.infrastructure.persistence.entity.PermissionEntity;

public final class PermissionPersistenceMapper {

	private PermissionPersistenceMapper() {
	}

	public static Permission toDomain(PermissionEntity entity) {
		if (entity == null) {
			return null;
		}

		return new Permission(
			entity.getId(),
			entity.getCode(),
			entity.getName(),
			entity.getResource(),
			entity.getAction(),
			entity.getCreatedAt()
		);
	}

	public static PermissionEntity toEntity(Permission domain) {
		if (domain == null) {
			return null;
		}

		PermissionEntity entity = new PermissionEntity();
		entity.setId(domain.id());
		entity.setCode(domain.code());
		entity.setName(domain.name());
		entity.setResource(domain.resource());
		entity.setAction(domain.action());
		return entity;
	}
}
