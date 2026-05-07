package com.dms.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dms.domain.entity.FileAttributeOption;

public interface FileAttributeOptionRepository extends JpaRepository<FileAttributeOption, Long> {

    // ===== BASIC =====

    List<FileAttributeOption> findByAttributeId(Long attributeId);

    List<FileAttributeOption> findByAttributeIdOrderBySortOrderAsc(Long attributeId);

    Optional<FileAttributeOption> findByAttributeIdAndOptionValue(Long attributeId, String optionValue);

    boolean existsByAttributeIdAndOptionValue(Long attributeId, String optionValue);

    // ===== BULK =====

    List<FileAttributeOption> findByAttributeIdIn(List<Long> attributeIds);

    // ===== DELETE =====

    void deleteByAttributeId(Long attributeId);
}