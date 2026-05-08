package com.baw.identity.infrastructure.persistence.repository;

import java.util.Optional;

import com.baw.identity.infrastructure.persistence.entity.IdentityProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityProviderRepository extends JpaRepository<IdentityProviderEntity, Long> {

	Optional<IdentityProviderEntity> findByProviderCode(String providerCode);
}
