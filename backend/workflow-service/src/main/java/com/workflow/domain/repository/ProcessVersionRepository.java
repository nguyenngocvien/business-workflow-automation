package com.workflow.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessVersion;
import com.workflow.domain.enums.DeployStatus;

@Repository
public interface ProcessVersionRepository extends JpaRepository<ProcessVersion, Long> {

    List<ProcessVersion> findByProcessDefinitionIdOrderByVersionDesc(Long processDefinitionId);

    Optional<ProcessVersion> findByProcessDefinitionIdAndVersion(Long processDefinitionId, Integer version);

    @Query("SELECT v FROM ProcessVersion v WHERE v.processDefinition.id = :processDefinitionId AND v.status = :status ORDER BY v.version DESC")
    List<ProcessVersion> findByProcessDefinitionIdAndStatusOrderByVersionDesc(Long processDefinitionId, DeployStatus status);
}