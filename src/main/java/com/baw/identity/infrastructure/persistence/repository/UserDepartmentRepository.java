package com.baw.identity.infrastructure.persistence.repository;

import com.baw.identity.infrastructure.persistence.entity.UserDepartmentEntity;
import com.baw.identity.infrastructure.persistence.entity.UserDepartmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDepartmentRepository extends JpaRepository<UserDepartmentEntity, UserDepartmentId> {
}
