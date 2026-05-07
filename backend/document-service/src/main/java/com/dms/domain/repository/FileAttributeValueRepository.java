package com.dms.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dms.domain.entity.FileAttributeValue;

public interface FileAttributeValueRepository extends JpaRepository<FileAttributeValue, Long> {

    List<FileAttributeValue> findByFileId(Long fileId);
}