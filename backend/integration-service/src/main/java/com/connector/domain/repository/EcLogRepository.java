package com.connector.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcLog;
import com.connector.domain.entity.EcLogId;

public interface EcLogRepository extends JpaRepository<EcLog, EcLogId> {
}
