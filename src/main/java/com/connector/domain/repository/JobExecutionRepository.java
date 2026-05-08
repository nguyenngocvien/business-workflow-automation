package com.connector.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.JobExecutionEntity;

public interface JobExecutionRepository extends JpaRepository<JobExecutionEntity, Long> {
}
