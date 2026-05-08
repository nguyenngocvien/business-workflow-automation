package com.baw.identity.infrastructure.persistence.repository;

import com.baw.identity.infrastructure.persistence.entity.RolePermissionEntity;
import com.baw.identity.infrastructure.persistence.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, RolePermissionId> {
}
