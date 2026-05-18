package com.dms.interfaces.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dms.application.usecase.FileAttributeValueUseCase;
import com.dms.interfaces.rest.request.FileAttributeValuesRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Attribute Values")
@RequiredArgsConstructor
public class FileAttributeValueController {

    private final FileAttributeValueUseCase useCase;

    @PostMapping("/{fileId}/attributes")
    public void create(
            @PathVariable Long fileId,
            @RequestBody FileAttributeValuesRequest req) {

        useCase.create(fileId, req);
    }

    @PutMapping("/{fileId}/attributes")
    public void update(
            @PathVariable Long fileId,
            @RequestBody FileAttributeValuesRequest req) {

        useCase.update(fileId, req);
    }
}
