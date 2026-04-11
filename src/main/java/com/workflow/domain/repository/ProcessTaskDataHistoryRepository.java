package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessTaskDataHistory;

@Repository
public interface ProcessTaskDataHistoryRepository extends JpaRepository<ProcessTaskDataHistory, Long> {
}