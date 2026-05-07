package com.dms.application.port.out;

import java.io.InputStream;
import java.util.List;

public interface StorageService {

        void upload(String bucket,
                        String objectKey,
                        InputStream inputStream,
                        long size,
                        String contentType) throws Exception;

        InputStream download(String bucket, String objectKey) throws Exception;

        void delete(String bucket, String objectKey) throws Exception;

        boolean exists(String bucket, String objectKey) throws Exception;

        String getPresignedUploadUrl(String bucket,
                        String objectKey,
                        int expirySeconds) throws Exception;

        String getPresignedUrl(String bucket,
                        String objectKey,
                        int expirySeconds) throws Exception;

        void copy(String sourceBucket,
                        String sourceKey,
                        String targetBucket,
                        String targetKey) throws Exception;

        String initiateMultipartUpload(String bucket, String objectKey) throws Exception;

        String getPresignedUploadPartUrl(String bucket,
                        String objectKey,
                        String uploadId,
                        int partNumber,
                        int expirySeconds) throws Exception;

        void completeMultipartUpload(String bucket,
                        String objectKey,
                        String uploadId,
                        List<PartETag> parts) throws Exception;

        void abortMultipartUpload(String bucket,
                        String objectKey,
                        String uploadId) throws Exception;
}