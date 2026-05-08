package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.IdentityProvider;

public interface IdentityProviderRepositoryPort {

	IdentityProvider save(IdentityProvider provider);

	Optional<IdentityProvider> findById(Long id);

	Optional<IdentityProvider> findByProviderCode(String providerCode);

	List<IdentityProvider> findAll();
}
