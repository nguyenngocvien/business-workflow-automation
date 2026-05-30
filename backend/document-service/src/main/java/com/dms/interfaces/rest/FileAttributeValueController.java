package com.dms.interfaces.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dms.application.usecase.FileAttributeValueUseCase;
import com.dms.interfaces.rest.request.FileAttributeValuesRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Attribute Values", description = "Create and update attribute values for uploaded files.")
@RequiredArgsConstructor
public class FileAttributeValueController {

    private final FileAttributeValueUseCase useCase;

    @PostMapping("/{fileId}/attributes")
    @Operation(
            operationId = "createFileAttributeValues",
            summary = "Create attribute values",
            description = "Creates attribute values for a file.")
    public void create(
            @PathVariable Long fileId,
            @RequestBody FileAttributeValuesRequest req) {

        useCase.create(fileId, req);
    }

    @PutMapping("/{fileId}/attributes")
    @Operation(
            operationId = "updateFileAttributeValues",
            summary = "Update attribute values",
            description = "Updates attribute values for a file.")
    public void update(
            @PathVariable Long fileId,
            @RequestBody FileAttributeValuesRequest req) {

        useCase.update(fileId, req);
    }
}
