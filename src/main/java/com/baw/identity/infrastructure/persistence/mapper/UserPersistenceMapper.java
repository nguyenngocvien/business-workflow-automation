package com.baw.identity.infrastructure.persistence.mapper;

import com.baw.identity.domain.model.User;
import com.baw.identity.infrastructure.persistence.entity.UserEntity;

public final class UserPersistenceMapper {

	private UserPersistenceMapper() {
	}

	public static User toDomain(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		return new User(
			entity.getId(),
			entity.getExternalId(),
			entity.getUsername(),
			entity.getEmail(),
			entity.getFirstName(),
			entity.getLastName(),
			entity.getFullName(),
			entity.getPhoneNumber(),
			entity.getStatus(),
			entity.getSource(),
			Boolean.TRUE.equals(entity.getIsDeleted()),
			entity.getCreatedAt(),
			entity.getUpdatedAt()
		);
	}

	public static UserEntity toEntity(User domain) {
		if (domain == null) {
			return null;
		}

		UserEntity entity = new UserEntity();
		entity.setId(domain.id());
		entity.setExternalId(domain.externalId());
		entity.setUsername(domain.username());
		entity.setEmail(domain.email());
		entity.setFirstName(domain.firstName());
		entity.setLastName(domain.lastName());
		entity.setFullName(domain.fullName());
		entity.setPhoneNumber(domain.phoneNumber());
		entity.setStatus(domain.status());
		entity.setSource(domain.source());
		entity.setIsDeleted(domain.deleted());
		return entity;
	}
}
