package com.baw.identity.infrastructure.persistence.repository;

import java.util.Optional;

import com.baw.identity.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByCode(String code);
}
