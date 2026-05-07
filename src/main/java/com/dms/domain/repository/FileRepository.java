package com.dms.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dms.domain.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long>,
        JpaSpecificationExecutor<FileEntity> {

    Optional<FileEntity> findByBucketNameAndObjectKey(String bucket, String key);

    Page<FileEntity> findByStatus(Short status, Pageable pageable);
}