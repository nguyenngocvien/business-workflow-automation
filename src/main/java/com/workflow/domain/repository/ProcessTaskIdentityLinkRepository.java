package com.workflow.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessTaskIdentityLink;

@Repository
public interface ProcessTaskIdentityLinkRepository extends JpaRepository<ProcessTaskIdentityLink, Long> {
    List<ProcessTaskIdentityLink> findByTask_IdOrderByCreatedAtAsc(Long taskId);
}
