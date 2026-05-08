package com.baw.identity.infrastructure.persistence.repository;

import java.util.Optional;

import com.baw.identity.infrastructure.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

	Optional<DepartmentEntity> findByCode(String code);
}
