package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessStepDefinition;

@Repository
public interface ProcessStepDefinitionRepository extends JpaRepository<ProcessStepDefinition, Long> {
}