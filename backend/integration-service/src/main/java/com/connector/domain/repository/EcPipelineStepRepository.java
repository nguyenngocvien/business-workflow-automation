package com.connector.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcPipelineStep;

public interface EcPipelineStepRepository extends JpaRepository<EcPipelineStep, Long> {
}
