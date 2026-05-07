package com.dms.application.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dms.application.command.FileSearchCommand;
import com.dms.application.result.FileSearchOptionsResult;
import com.dms.application.result.FileSearchResult;
import com.dms.application.usecase.FileSearchUseCase;
import com.dms.domain.entity.AttributeFilter;
import com.dms.domain.entity.FileAttribute;
import com.dms.domain.entity.FileEntity;
import com.dms.domain.enums.AttributeDataType;
import com.dms.domain.repository.FileAttributeRepository;
import com.dms.domain.repository.FileCategoryRepository;
import com.dms.domain.repository.FileRepository;
import com.dms.domain.specification.FileSpecification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileSearchService implements FileSearchUseCase {

    private final FileRepository fileRepository;
    private final FileAttributeRepository attributeRepository;
    private final FileCategoryRepository categoryRepository;

    @Override
    public Page<FileSearchResult> search(FileSearchCommand cmd) {

        // ===== 1. Validate =====
        if (cmd.getSize() > 100) {
            throw new IllegalArgumentException("Page size too large");
        }

        if (cmd.getFilters() != null && cmd.getFilters().size() > 5) {
            throw new IllegalArgumentException("Too many filters");
        }

        // ===== 2. Load attribute type map =====
        Map<String, AttributeDataType> attrTypeMap = loadAttributeTypeMap(cmd.getFilters());

        // ===== 3. Build specification =====
        Specification<FileEntity> spec = Specification.where((Specification<FileEntity>) null);

        if (cmd.getStatus() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), cmd.getStatus()));
        }

        if (cmd.getCategoryCode() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.join("category").get("code"), cmd.getCategoryCode()));
        }

        if (cmd.getFilters() != null && !cmd.getFilters().isEmpty()) {
            spec = spec.and(FileSpecification.byAttributeFilters(
                    cmd.getFilters(),
                    attrTypeMap
            ));
        }

        // ===== 4. Pageable =====
        Pageable pageable = PageRequest.of(
                cmd.getPage(),
                cmd.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // ===== 5. Execute =====
        return fileRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Override
    public FileSearchOptionsResult getSearchOptions() {

        List<String> categories = categoryRepository.findAll()
                .stream()
                .map(c -> c.getCode())
                .toList();

        List<FileSearchOptionsResult.AttributeOptionDto> attributes =
                attributeRepository.findAll().stream()
                        .map(a -> new FileSearchOptionsResult.AttributeOptionDto(
                                a.getKeyCode(),
                                a.getDisplayName(),
                                a.getDataType().name()
                        ))
                        .toList();

        return new FileSearchOptionsResult(categories, attributes);
    }

    // =========================
    // HELPERS
    // =========================

    private Map<String, AttributeDataType> loadAttributeTypeMap(List<AttributeFilter> filters) {

        if (filters == null || filters.isEmpty()) {
            return Map.of();
        }

        List<String> keys = filters.stream()
                .map(AttributeFilter::getKey)
                .distinct()
                .toList();

        return attributeRepository.findAll().stream()
                .filter(a -> keys.contains(a.getKeyCode()))
                .collect(Collectors.toMap(
                        FileAttribute::getKeyCode,
                        FileAttribute::getDataType
                ));
    }

    private FileSearchResult toResponse(FileEntity e) {
        return new FileSearchResult(
                e.getId(),
                e.getFileName(),
                e.getContentType(),
                e.getFileSize(),
                e.getCreatedAt()
        );
    }
}