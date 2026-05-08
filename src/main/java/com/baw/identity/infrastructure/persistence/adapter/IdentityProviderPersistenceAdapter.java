package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.out.IdentityProviderRepositoryPort;
import com.baw.identity.domain.model.IdentityProvider;
import com.baw.identity.infrastructure.persistence.mapper.IdentityProviderPersistenceMapper;
import com.baw.identity.infrastructure.persistence.repository.IdentityProviderRepository;
import org.springframework.stereotype.Component;

@Component
public class IdentityProviderPersistenceAdapter implements IdentityProviderRepositoryPort {

	private final IdentityProviderRepository identityProviderRepository;

	public IdentityProviderPersistenceAdapter(IdentityProviderRepository identityProviderRepository) {
		this.identityProviderRepository = identityProviderRepository;
	}

	@Override
	public IdentityProvider save(IdentityProvider provider) {
		return IdentityProviderPersistenceMapper.toDomain(
			identityProviderRepository.save(IdentityProviderPersistenceMapper.toEntity(provider))
		);
	}

	@Override
	public Optional<IdentityProvider> findById(Long id) {
		return identityProviderRepository.findById(id).map(IdentityProviderPersistenceMapper::toDomain);
	}

	@Override
	public Optional<IdentityProvider> findByProviderCode(String providerCode) {
		return identityProviderRepository.findByProviderCode(providerCode).map(IdentityProviderPersistenceMapper::toDomain);
	}

	@Override
	public List<IdentityProvider> findAll() {
		return identityProviderRepository.findAll().stream().map(IdentityProviderPersistenceMapper::toDomain).toList();
	}
}
