package com.baw.identity.infrastructure.persistence.repository;

import java.util.Optional;

import com.baw.identity.infrastructure.persistence.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

	Optional<GroupEntity> findByCode(String code);
}
