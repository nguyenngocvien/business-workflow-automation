package com.dms.application.usecase;

import org.springframework.stereotype.Component;

import com.dms.application.command.UpsertFileAttributesCommand;
import com.dms.application.service.FileAttributeValueService;
import com.dms.interfaces.rest.request.FileAttributeValuesRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileAttributeValueUseCase {

    private final FileAttributeValueService service;

    public void create(Long fileId, FileAttributeValuesRequest req) {
        service.upsert(new UpsertFileAttributesCommand(fileId, req.getAttributes()));
    }

    public void update(Long fileId, FileAttributeValuesRequest req) {
        service.upsert(new UpsertFileAttributesCommand(fileId, req.getAttributes()));
    }
}