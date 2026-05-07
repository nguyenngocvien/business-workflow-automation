package com.dms.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.dms.application.command.CompleteUploadCommand;
import com.dms.application.command.PresignedUploadCommand;
import com.dms.application.command.UpdateFileCommand;
import com.dms.application.result.FileResult;
import com.dms.application.result.InitiateMultipartResult;
import com.dms.application.result.PresignedUploadResult;
import com.dms.application.service.FileService;
import com.dms.domain.entity.FileEntity;
import com.dms.interfaces.rest.request.CompleteMultipartRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileUseCase {

    private final FileService service;

    public PresignedUploadResult presignedUpload(PresignedUploadCommand cmd) {
        return service.generatePresignedUpload(cmd);
    }

    public FileResult completeUpload(CompleteUploadCommand cmd) {
        return toResponse(service.completeUpload(cmd));
    }

    public String presignedDownload(Long fileId) {
        return service.generatePresignedDownload(fileId);
    }

    public InitiateMultipartResult initiateMultipart(String categoryCode) {
        return service.initiateMultipart(categoryCode);
    }

    public FileEntity completeMultipartUpload(CompleteMultipartRequest req) {
        return service.completeMultipartUpload(req);
    }

    public String getPresignedPartUrl(String bucket, String objectKey, String uploadId, int partNumber) {
        return service.getPresignedPartUrl(bucket, objectKey, uploadId, partNumber);
    }

    public FileResult get(Long id) {
        return toResponse(service.get(id));
    }

    public Page<FileResult> list(Pageable pageable) {
        return service.list(pageable).map(this::toResponse);
    }

    public FileResult update(UpdateFileCommand cmd) {
        return toResponse(service.update(cmd));
    }

    public void delete(Long id) {
        service.delete(id);
    }

    private FileResult toResponse(FileEntity e) {
        return new FileResult(
                e.getId(),
                e.getFileName(),
                e.getContentType(),
                e.getFileSize(),
                e.getCreatedAt()
        );
    }
}