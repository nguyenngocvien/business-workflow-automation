package com.baw.identity.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baw.identity.infrastructure.persistence.entity.SyncJobEntity;

public interface SyncJobRepository extends JpaRepository<SyncJobEntity, Long> {
}
