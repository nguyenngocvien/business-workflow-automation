package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessTimer;

@Repository
public interface ProcessTimerRepository extends JpaRepository<ProcessTimer, Long> {
}