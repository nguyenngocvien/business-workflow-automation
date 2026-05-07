package com.dms.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dms.domain.entity.FileAccessLog;

public interface FileAccessLogRepository extends JpaRepository<FileAccessLog, Long> {

    List<FileAccessLog> findByFileId(Long fileId);
}