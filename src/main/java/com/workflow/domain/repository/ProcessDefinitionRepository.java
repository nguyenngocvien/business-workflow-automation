package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessDefinition;

@Repository
public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinition, Long> {
    ProcessDefinition findByProcessKey(String processKey);
}