package com.baw.identity.application.port.in;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import com.baw.identity.domain.model.IdentityProvider;

public interface IdentityProviderUseCase {

	IdentityProvider create(CreateIdentityProviderCommand command);

	IdentityProvider update(Long id, UpdateIdentityProviderCommand command);

	Optional<IdentityProvider> findById(Long id);

	Optional<IdentityProvider> findByProviderCode(String providerCode);

	List<IdentityProvider> findAll();

	record CreateIdentityProviderCommand(
		String providerCode,
		String providerName,
		String providerType,
		Map<String, Object> config,
		Boolean active
	) {
	}

	record UpdateIdentityProviderCommand(
		String providerCode,
		String providerName,
		String providerType,
		Map<String, Object> config,
		Boolean active
	) {
	}
}
