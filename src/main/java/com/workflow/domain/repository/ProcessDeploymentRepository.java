package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessDeployment;

@Repository
public interface ProcessDeploymentRepository extends JpaRepository<ProcessDeployment, Long> {
}