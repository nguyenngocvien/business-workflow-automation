package com.dms.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dms.application.command.AddContentTypeCommand;
import com.dms.application.command.CreateFileCategoryCommand;
import com.dms.application.command.UpdateFileCategoryCommand;
import com.dms.domain.entity.FileCategory;
import com.dms.domain.entity.FileCategoryType;
import com.dms.domain.repository.FileCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileCategoryService {

    private final FileCategoryRepository repository;

    public FileCategory create(CreateFileCategoryCommand cmd) {

        repository.findByCode(cmd.getCode())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Category already exists");
                });

        FileCategory entity = new FileCategory();
        entity.setCode(cmd.getCode());
        entity.setName(cmd.getName());
        entity.setMaxSize(cmd.getMaxSize());
        entity.setBucketName(cmd.getBucketName());
        entity.setRetentionDays(cmd.getRetentionDays());
        entity.setIsPublic(cmd.getIsPublic());
        entity.setCreatedAt(Instant.now());

        return repository.save(entity);
    }

    public List<FileCategory> findAll() {
        return repository.findAll();
    }

    public FileCategory update(UpdateFileCategoryCommand cmd) {

        FileCategory entity = repository.findById(cmd.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        entity.setName(cmd.getName());
        entity.setMaxSize(cmd.getMaxSize());
        entity.setBucketName(cmd.getBucketName());
        entity.setRetentionDays(cmd.getRetentionDays());
        entity.setIsPublic(cmd.getIsPublic());

        return entity;
    }

    public void addContentType(AddContentTypeCommand cmd) {

        FileCategory category = repository.findById(cmd.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        boolean exists = category.getContentTypes().stream()
                .anyMatch(ct -> ct.getContentType().equals(cmd.getContentType()));

        if (exists) {
            throw new IllegalArgumentException("Content type already exists");
        }

        FileCategoryType type = new FileCategoryType();
        type.setContentType(cmd.getContentType());
        type.setCategory(category);

        category.getContentTypes().add(type);
    }
}