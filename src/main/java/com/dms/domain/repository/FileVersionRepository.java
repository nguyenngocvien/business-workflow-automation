package com.dms.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dms.domain.entity.FileVersion;

public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {

    List<FileVersion> findByFileIdOrderByVersionDesc(Long fileId);
}