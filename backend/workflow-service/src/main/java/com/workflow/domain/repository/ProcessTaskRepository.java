package com.workflow.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.UserTask;

@Repository
public interface ProcessTaskRepository extends JpaRepository<UserTask, Long> {
    List<UserTask> findByTaskIdOrderByCreatedAtDesc(String taskId);
    List<UserTask> findByProcessInstance_IdOrderByCreatedAtDesc(Long processInstanceId);
}
