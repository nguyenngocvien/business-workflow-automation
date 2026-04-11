package com.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workflow.domain.entity.ProcessAttachment;

@Repository
public interface ProcessAttachmentRepository extends JpaRepository<ProcessAttachment, Long> {
}