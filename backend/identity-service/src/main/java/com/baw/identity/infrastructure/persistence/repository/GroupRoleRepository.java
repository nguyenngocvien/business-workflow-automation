package com.baw.identity.infrastructure.persistence.repository;

import com.baw.identity.infrastructure.persistence.entity.GroupRoleEntity;
import com.baw.identity.infrastructure.persistence.entity.GroupRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRoleRepository extends JpaRepository<GroupRoleEntity, GroupRoleId> {
}
