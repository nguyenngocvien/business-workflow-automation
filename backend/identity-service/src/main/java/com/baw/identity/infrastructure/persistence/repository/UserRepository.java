package com.baw.identity.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import com.baw.identity.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsername(String username);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByExternalId(UUID externalId);
}
