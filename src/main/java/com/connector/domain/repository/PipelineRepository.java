package com.connector.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.PipelineEntity;

public interface PipelineRepository extends JpaRepository<PipelineEntity, Long> {

    Optional<PipelineEntity> findByPipelineCode(String pipelineCode);
}
