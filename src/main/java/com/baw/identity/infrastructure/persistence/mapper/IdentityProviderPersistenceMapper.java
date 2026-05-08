package com.baw.identity.infrastructure.persistence.mapper;

import com.baw.identity.domain.model.IdentityProvider;
import com.baw.identity.infrastructure.persistence.entity.IdentityProviderEntity;

public final class IdentityProviderPersistenceMapper {

	private IdentityProviderPersistenceMapper() {
	}

	public static IdentityProvider toDomain(IdentityProviderEntity entity) {
		if (entity == null) {
			return null;
		}

		return new IdentityProvider(
			entity.getId(),
			entity.getProviderCode(),
			entity.getProviderName(),
			entity.getProviderType(),
			entity.getConfig(),
			Boolean.TRUE.equals(entity.getIsActive()),
			entity.getCreatedAt()
		);
	}

	public static IdentityProviderEntity toEntity(IdentityProvider domain) {
		if (domain == null) {
			return null;
		}

		IdentityProviderEntity entity = new IdentityProviderEntity();
		entity.setId(domain.id());
		entity.setProviderCode(domain.providerCode());
		entity.setProviderName(domain.providerName());
		entity.setProviderType(domain.providerType());
		entity.setConfig(domain.config());
		entity.setIsActive(domain.active());
		return entity;
	}
}
