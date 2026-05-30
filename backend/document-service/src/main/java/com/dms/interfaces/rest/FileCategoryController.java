package com.dms.interfaces.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dms.application.command.AddContentTypeCommand;
import com.dms.application.command.CreateFileCategoryCommand;
import com.dms.application.command.UpdateFileCategoryCommand;
import com.dms.application.result.FileCategoryResult;
import com.dms.application.usecase.FileCategoryUseCase;
import com.dms.interfaces.rest.request.AddContentTypeRequest;
import com.dms.interfaces.rest.request.CreateFileCategoryRequest;
import com.dms.interfaces.rest.request.UpdateFileCategoryRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/file-categories")
@Tag(name = "File Categories", description = "Manage file categories and supported content types.")
@RequiredArgsConstructor
public class FileCategoryController {

    private final FileCategoryUseCase useCase;

    @PostMapping
    @Operation(
            operationId = "createFileCategory",
            summary = "Create a file category",
            description = "Creates a new file category.")
    public FileCategoryResult create(@RequestBody CreateFileCategoryRequest req) {

        return useCase.create(new CreateFileCategoryCommand(
                req.getCode(),
                req.getName(),
                req.getMaxSize(),
                req.getBucketName(),
                req.getRetentionDays(),
                req.getIsPublic()
        ));
    }

    @GetMapping
    @Operation(
            operationId = "listFileCategories",
            summary = "List file categories",
            description = "Returns all configured file categories.")
    public List<FileCategoryResult> getAll() {
        return useCase.getAll();
    }

    @PutMapping("/{id}")
    @Operation(
            operationId = "updateFileCategory",
            summary = "Update a file category",
            description = "Updates an existing file category.")
    public FileCategoryResult update(
            @PathVariable Long id,
            @RequestBody UpdateFileCategoryRequest req) {

        return useCase.update(new UpdateFileCategoryCommand(
                id,
                req.getName(),
                req.getMaxSize(),
                req.getBucketName(),
                req.getRetentionDays(),
                req.getIsPublic()
        ));
    }

    @PostMapping("/{id}/content-types")
    @Operation(
            operationId = "addFileCategoryContentType",
            summary = "Add a content type",
            description = "Adds a supported content type to a file category.")
    public void addContentType(
            @PathVariable Long id,
            @RequestBody AddContentTypeRequest req) {

        useCase.addContentType(new AddContentTypeCommand(
                id,
                req.getContentType()
        ));
    }
}
