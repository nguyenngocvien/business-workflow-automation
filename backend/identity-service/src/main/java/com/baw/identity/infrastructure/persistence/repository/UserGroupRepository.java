package com.baw.identity.infrastructure.persistence.repository;

import com.baw.identity.infrastructure.persistence.entity.UserGroupEntity;
import com.baw.identity.infrastructure.persistence.entity.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroupEntity, UserGroupId> {
}
