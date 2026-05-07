package com.dms.interfaces.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dms.application.command.CompleteUploadCommand;
import com.dms.application.command.PresignedUploadCommand;
import com.dms.application.command.UpdateFileCommand;
import com.dms.application.result.FileResult;
import com.dms.application.result.InitiateMultipartResult;
import com.dms.application.result.PresignedPartResult;
import com.dms.application.result.PresignedUploadResult;
import com.dms.application.usecase.FileUseCase;
import com.dms.domain.entity.FileEntity;
import com.dms.interfaces.rest.request.CompleteMultipartRequest;
import com.dms.interfaces.rest.request.CompleteUploadRequest;
import com.dms.interfaces.rest.request.CreateFileRequest;
import com.dms.interfaces.rest.request.InitiateMultipartRequest;
import com.dms.interfaces.rest.request.PresignedPartRequest;
import com.dms.interfaces.rest.request.PresignedUploadRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUseCase useCase;

    @PostMapping("/presigned-upload")
    public PresignedUploadResult presignedUpload(@RequestBody PresignedUploadRequest req) {
        return useCase.presignedUpload(
                new PresignedUploadCommand(
                        req.getFileName(),
                        req.getContentType(),
                        req.getCategoryCode()));
    }

    @PostMapping("/complete-upload")
    public FileResult completeUpload(@RequestBody CompleteUploadRequest req) {
        return useCase.completeUpload(
                new CompleteUploadCommand(
                        req.getObjectKey(),
                        req.getFileName(),
                        req.getSize(),
                        req.getContentType(),
                        req.getCategoryCode()));
    }

    @GetMapping("/{id}/presigned-download")
    public String presignedDownload(@PathVariable Long id) {
        return useCase.presignedDownload(id);
    }

    @PostMapping("/multipart/init")
    public InitiateMultipartResult init(@RequestBody InitiateMultipartRequest req) {
        return useCase.initiateMultipart(req.getCategoryCode());
    }

    @PostMapping("/multipart/presigned-part")
    public PresignedPartResult presignedPart(@RequestBody PresignedPartRequest req) {

        String url = useCase.getPresignedPartUrl(
                req.getBucket(),
                req.getObjectKey(),
                req.getUploadId(),
                req.getPartNumber());

        return new PresignedPartResult(url);
    }

    @PostMapping("/multipart/complete")
    public FileResult complete(@RequestBody CompleteMultipartRequest req) {

        FileEntity file = useCase.completeMultipartUpload(req);

        return new FileResult(
                file.getId(),
                file.getFileName(),
                file.getContentType(),
                file.getFileSize(),
                file.getCreatedAt());
    }

    @GetMapping("/{id}")
    public FileResult get(@PathVariable Long id) {
        return useCase.get(id);
    }

    @GetMapping
    public Page<FileResult> list(Pageable pageable) {
        return useCase.list(pageable);
    }

    @PutMapping("/{id}")
    public FileResult update(@PathVariable Long id, @RequestBody CreateFileRequest req) {
        return useCase.update(new UpdateFileCommand(id, req.getFileName()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        useCase.delete(id);
    }
}