package com.dms.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dms.domain.entity.FileAttribute;

public interface FileAttributeRepository extends JpaRepository<FileAttribute, Long> {

    Optional<FileAttribute> findByKeyCode(String keyCode);
}