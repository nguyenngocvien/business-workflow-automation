package com.dms.application.service;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dms.application.command.CompleteUploadCommand;
import com.dms.application.command.PresignedUploadCommand;
import com.dms.application.command.UpdateFileCommand;
import com.dms.application.port.out.StorageService;
import com.dms.application.result.InitiateMultipartResult;
import com.dms.application.result.PresignedUploadResult;
import com.dms.domain.entity.FileCategory;
import com.dms.domain.entity.FileEntity;
import com.dms.domain.repository.FileCategoryRepository;
import com.dms.domain.repository.FileRepository;
import com.dms.interfaces.rest.request.CompleteMultipartRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final FileCategoryRepository categoryRepository;
    private final StorageService storageService;

    // ===== PRESIGNED UPLOAD =====

    public PresignedUploadResult generatePresignedUpload(PresignedUploadCommand cmd) {

        FileCategory category = categoryRepository.findByCode(cmd.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        String objectKey = "files/" + UUID.randomUUID();

        try {
            String uploadUrl = storageService.getPresignedUploadUrl(
                    category.getBucketName(),
                    objectKey,
                    300 // 5 phút
            );

            return new PresignedUploadResult(
                    uploadUrl,
                    objectKey,
                    category.getBucketName());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned upload URL", e);
        }
    }

    // ===== COMPLETE UPLOAD =====

    public FileEntity completeUpload(CompleteUploadCommand cmd) {

        FileCategory category = categoryRepository.findByCode(cmd.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        try {
            // 🔥 VERIFY OBJECT EXISTS (QUAN TRỌNG)
            boolean exists = storageService.exists(
                    category.getBucketName(),
                    cmd.getObjectKey());

            if (!exists) {
                throw new IllegalArgumentException("Uploaded file not found in storage");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify uploaded file", e);
        }

        FileEntity file = new FileEntity();
        file.setFileName(cmd.getFileName());
        file.setObjectKey(cmd.getObjectKey());
        file.setBucketName(category.getBucketName());
        file.setContentType(cmd.getContentType());
        file.setFileSize(cmd.getSize());
        file.setCreatedAt(Instant.now());
        file.setCategory(category);
        file.setStatus((short) 1);

        return fileRepository.save(file);
    }

    // ===== PRESIGNED DOWNLOAD =====

    public String generatePresignedDownload(Long fileId) {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        try {
            return storageService.getPresignedUrl(
                    file.getBucketName(),
                    file.getObjectKey(),
                    300);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned download URL", e);
        }
    }

    // ===== DIRECT DOWNLOAD (OPTIONAL) =====

    public InputStream download(Long fileId) {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        try {
            return storageService.download(
                    file.getBucketName(),
                    file.getObjectKey());
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

    // ===== CRUD =====

    public FileEntity get(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    public Page<FileEntity> list(Pageable pageable) {
        return fileRepository.findAll(pageable);
    }

    public FileEntity update(UpdateFileCommand cmd) {
        FileEntity file = get(cmd.getFileId());
        file.setFileName(cmd.getFileName());
        file.setUpdatedAt(Instant.now());
        return file;
    }

    public void delete(Long id) {

        FileEntity file = get(id);

        try {
            // 🔥 OPTIONAL: delete physical file
            storageService.delete(file.getBucketName(), file.getObjectKey());
        } catch (Exception e) {
            // log thôi, không fail business
        }

        file.setStatus((short) 2);
        file.setDeletedAt(Instant.now());
    }

    // ===== MULTIPART UPLOAD =====

    public InitiateMultipartResult initiateMultipart(String categoryCode) {

        FileCategory category = categoryRepository.findByCode(categoryCode)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        String objectKey = "files/" + UUID.randomUUID();

        try {
            String uploadId = storageService.initiateMultipartUpload(
                    category.getBucketName(),
                    objectKey);

            return new InitiateMultipartResult(
                    uploadId,
                    objectKey,
                    category.getBucketName());

        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate multipart upload", e);
        }
    }

    public String getPresignedPartUrl(String bucket, String objectKey, String uploadId, int partNumber) {

        try {
            return storageService.getPresignedUploadPartUrl(
                    bucket,
                    objectKey,
                    uploadId,
                    partNumber,
                    300);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get presigned part URL", e);
        }
    }

    public FileEntity completeMultipartUpload(CompleteMultipartRequest req) {

        FileCategory category = categoryRepository.findByCode(req.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        try {
            storageService.completeMultipartUpload(
                    category.getBucketName(),
                    req.getObjectKey(),
                    req.getUploadId(),
                    req.getParts());

        } catch (Exception e) {
            throw new RuntimeException("Failed to complete multipart upload", e);
        }

        // save DB
        FileEntity file = new FileEntity();
        file.setObjectKey(req.getObjectKey());
        file.setBucketName(category.getBucketName());
        file.setFileName(req.getFileName());
        file.setContentType(req.getContentType());
        file.setFileSize(req.getSize());
        file.setCreatedAt(Instant.now());
        file.setCategory(category);
        file.setStatus((short) 1);

        return fileRepository.save(file);
    }
}