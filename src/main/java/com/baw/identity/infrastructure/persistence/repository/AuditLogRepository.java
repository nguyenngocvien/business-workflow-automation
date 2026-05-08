package com.baw.identity.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baw.identity.infrastructure.persistence.entity.AuditLogEntity;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
