package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.UserTaskAssignmentHistory;

@Repository
public interface ProcessTaskAssignmentHistoryRepository extends JpaRepository<UserTaskAssignmentHistory, Long> {
}