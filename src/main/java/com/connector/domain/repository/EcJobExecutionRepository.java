package com.connector.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcJobExecution;

public interface EcJobExecutionRepository extends JpaRepository<EcJobExecution, Long> {
}
