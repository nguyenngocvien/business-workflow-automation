package com.workflow.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessData;

@Repository
public interface ProcessTaskDataRepository extends JpaRepository<ProcessData, Long> {
    Optional<ProcessData> findByTask_Id(Long taskId);
}
