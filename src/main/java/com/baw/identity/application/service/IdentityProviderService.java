package com.baw.identity.application.service;

import java.util.List;
import java.util.Optional;

import com.baw.identity.application.port.in.IdentityProviderUseCase;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.application.port.out.IdentityProviderRepositoryPort;
import com.baw.identity.domain.model.IdentityProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IdentityProviderService implements IdentityProviderUseCase {

	private final IdentityProviderRepositoryPort identityProviderRepositoryPort;

	public IdentityProviderService(IdentityProviderRepositoryPort identityProviderRepositoryPort) {
		this.identityProviderRepositoryPort = identityProviderRepositoryPort;
	}

	@Override
	public IdentityProvider create(CreateIdentityProviderCommand command) {
		IdentityProvider provider = new IdentityProvider(
			null,
			command.providerCode(),
			command.providerName(),
			command.providerType(),
			command.config(),
			command.active() == null || command.active(),
			null
		);
		return identityProviderRepositoryPort.save(provider);
	}

	@Override
	public IdentityProvider update(Long id, UpdateIdentityProviderCommand command) {
		IdentityProvider existing = identityProviderRepositoryPort.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Identity provider not found: " + id));

		IdentityProvider updated = new IdentityProvider(
			existing.id(),
			command.providerCode() != null ? command.providerCode() : existing.providerCode(),
			command.providerName() != null ? command.providerName() : existing.providerName(),
			command.providerType() != null ? command.providerType() : existing.providerType(),
			command.config() != null ? command.config() : existing.config(),
			command.active() != null ? command.active() : existing.active(),
			existing.createdAt()
		);
		return identityProviderRepositoryPort.save(updated);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<IdentityProvider> findById(Long id) {
		return identityProviderRepositoryPort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<IdentityProvider> findByProviderCode(String providerCode) {
		return identityProviderRepositoryPort.findByProviderCode(providerCode);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentityProvider> findAll() {
		return identityProviderRepositoryPort.findAll();
	}
}
