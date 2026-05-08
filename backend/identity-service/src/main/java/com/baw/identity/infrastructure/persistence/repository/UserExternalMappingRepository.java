package com.baw.identity.infrastructure.persistence.repository;

import java.util.Optional;

import com.baw.identity.infrastructure.persistence.entity.UserExternalMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExternalMappingRepository extends JpaRepository<UserExternalMappingEntity, Long> {

	Optional<UserExternalMappingEntity> findByProvider_IdAndExternalUserId(Long providerId, String externalUserId);
}
