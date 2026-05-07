package com.dms.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dms.domain.entity.FileCategory;

public interface FileCategoryRepository extends JpaRepository<FileCategory, Long> {

    Optional<FileCategory> findByCode(String code);
}