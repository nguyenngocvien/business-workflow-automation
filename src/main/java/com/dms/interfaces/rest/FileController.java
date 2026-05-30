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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "Upload, download, update, and delete files.")
@RequiredArgsConstructor
public class FileController {

    private final FileUseCase useCase;

    @PostMapping("/presigned-upload")
    @Operation(
            operationId = "createPresignedUpload",
            summary = "Create a presigned upload",
            description = "Generates a presigned upload URL for a file.")
    public PresignedUploadResult createPresignedUpload(@RequestBody PresignedUploadRequest req) {
        return useCase.presignedUpload(
                new PresignedUploadCommand(
                        req.getFileName(),
                        req.getContentType(),
                        req.getCategoryCode()));
    }

    @PostMapping("/complete-upload")
    @Operation(
            operationId = "completeUpload",
            summary = "Complete a file upload",
            description = "Completes a direct upload and persists the resulting file metadata.")
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
    @Operation(
            operationId = "createPresignedDownload",
            summary = "Create a presigned download",
            description = "Generates a presigned download URL for the requested file.")
    public String createPresignedDownload(@PathVariable Long id) {
        return useCase.presignedDownload(id);
    }

    @PostMapping("/multipart/init")
    @Operation(
            operationId = "initMultipartUpload",
            summary = "Initiate a multipart upload",
            description = "Starts a multipart upload flow for a file category.")
    public InitiateMultipartResult init(@RequestBody InitiateMultipartRequest req) {
        return useCase.initiateMultipart(req.getCategoryCode());
    }

    @PostMapping("/multipart/presigned-part")
    @Operation(
            operationId = "createPresignedPart",
            summary = "Create a presigned multipart part",
            description = "Generates a presigned URL for uploading one multipart segment.")
    public PresignedPartResult createPresignedPart(@RequestBody PresignedPartRequest req) {

        String url = useCase.getPresignedPartUrl(
                req.getBucket(),
                req.getObjectKey(),
                req.getUploadId(),
                req.getPartNumber());

        return new PresignedPartResult(url);
    }

    @PostMapping("/multipart/complete")
    @Operation(
            operationId = "completeMultipartUpload",
            summary = "Complete a multipart upload",
            description = "Completes a multipart upload and returns the stored file metadata.")
    public FileResult completeMultipartUpload(@RequestBody CompleteMultipartRequest req) {

        FileEntity file = useCase.completeMultipartUpload(req);

        return new FileResult(
                file.getId(),
                file.getFileName(),
                file.getContentType(),
                file.getFileSize(),
                file.getCreatedAt());
    }

    @GetMapping("/{id}")
    @Operation(
            operationId = "getFile",
            summary = "Get a file",
            description = "Returns the file metadata for the requested identifier.")
    public FileResult get(@PathVariable Long id) {
        return useCase.get(id);
    }

    @GetMapping
    @Operation(
            operationId = "listFiles",
            summary = "List files",
            description = "Returns a paginated list of files.")
    public Page<FileResult> list(Pageable pageable) {
        return useCase.list(pageable);
    }

    @PutMapping("/{id}")
    @Operation(
            operationId = "updateFile",
            summary = "Update a file",
            description = "Updates the file name for the requested file.")
    public FileResult update(@PathVariable Long id, @RequestBody CreateFileRequest req) {
        return useCase.update(new UpdateFileCommand(id, req.getFileName()));
    }

    @DeleteMapping("/{id}")
    @Operation(
            operationId = "deleteFile",
            summary = "Delete a file",
            description = "Deletes the requested file.")
    public void delete(@PathVariable Long id) {
        useCase.delete(id);
    }
}
