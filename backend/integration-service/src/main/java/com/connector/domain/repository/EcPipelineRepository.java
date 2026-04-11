package com.connector.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcPipeline;

public interface EcPipelineRepository extends JpaRepository<EcPipeline, Long> {

    Optional<EcPipeline> findByPipelineCode(String pipelineCode);
}
