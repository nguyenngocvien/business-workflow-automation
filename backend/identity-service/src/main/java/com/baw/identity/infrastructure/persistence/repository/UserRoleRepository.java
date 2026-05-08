package com.baw.identity.infrastructure.persistence.repository;

import com.baw.identity.infrastructure.persistence.entity.UserRoleEntity;
import com.baw.identity.infrastructure.persistence.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
}
