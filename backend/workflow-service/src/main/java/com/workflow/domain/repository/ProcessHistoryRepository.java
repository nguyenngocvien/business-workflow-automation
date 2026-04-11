package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessHistory;

@Repository
public interface ProcessHistoryRepository extends JpaRepository<ProcessHistory, Long> {
}