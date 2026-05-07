package com.dms.application.usecase;

import java.util.UUID;

import com.dms.application.command.InitiateUploadCommand;
import com.dms.application.port.out.StorageService;
import com.dms.application.result.InitiateUploadResult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InitiateMultipartUploadUseCase {

    private final StorageService storageService;

    public InitiateUploadResult execute(InitiateUploadCommand cmd) throws Exception {
        String objectKey = generateObjectKey(cmd.getFileName());

        String uploadId = storageService
                .initiateMultipartUpload(cmd.getBucket(), objectKey);

        return new InitiateUploadResult(objectKey, uploadId);
    }

    private String generateObjectKey(String fileName) {
        return UUID.randomUUID() + "-" + fileName;
    }
}
