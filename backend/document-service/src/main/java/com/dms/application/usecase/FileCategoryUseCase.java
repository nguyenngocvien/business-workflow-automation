package com.dms.application.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dms.application.command.AddContentTypeCommand;
import com.dms.application.command.CreateFileCategoryCommand;
import com.dms.application.command.UpdateFileCategoryCommand;
import com.dms.application.result.FileCategoryResult;
import com.dms.application.service.FileCategoryService;
import com.dms.domain.entity.FileCategory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileCategoryUseCase {

    private final FileCategoryService service;

    public FileCategoryResult create(CreateFileCategoryCommand cmd) {
        FileCategory entity = service.create(cmd);
        return toResponse(entity);
    }

    public List<FileCategoryResult> getAll() {
        return service.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FileCategoryResult update(UpdateFileCategoryCommand cmd) {
        FileCategory entity = service.update(cmd);
        return toResponse(entity);
    }

    public void addContentType(AddContentTypeCommand cmd) {
        service.addContentType(cmd);
    }

    private FileCategoryResult toResponse(FileCategory e) {
        return new FileCategoryResult(
                e.getId(),
                e.getCode(),
                e.getName(),
                e.getIsPublic()
        );
    }
}