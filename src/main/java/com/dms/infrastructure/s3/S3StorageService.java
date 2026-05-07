package com.dms.infrastructure.s3;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dms.application.port.out.PartETag;
import com.dms.application.port.out.StorageService;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

@Service
public class S3StorageService implements StorageService {

        private final S3Client s3Client;
        private final S3Presigner s3Presigner;

        public S3StorageService(
                        S3Client s3Client,
                        S3Presigner s3Presigner) {
                this.s3Client = s3Client;
                this.s3Presigner = s3Presigner;
        }

        @Override
        public void upload(String bucket, String objectKey, InputStream inputStream, long size, String contentType)
                        throws Exception {

                PutObjectRequest request = PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .contentType(contentType)
                                .build();

                s3Client.putObject(request, RequestBody.fromInputStream(inputStream, size));
        }

        @Override
        public InputStream download(String bucket, String objectKey) throws Exception {

                GetObjectRequest request = GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build();

                ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
                return response;
        }

        @Override
        public void delete(String bucket, String objectKey) throws Exception {

                DeleteObjectRequest request = DeleteObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build();

                s3Client.deleteObject(request);
        }

        @Override
        public boolean exists(String bucket, String objectKey) throws Exception {

                try {
                        HeadObjectRequest request = HeadObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(objectKey)
                                        .build();

                        s3Client.headObject(request);
                        return true;

                } catch (S3Exception e) {
                        return false;
                }
        }

        // ========================
        // PRESIGNED URL
        // ========================

        @Override
        public String getPresignedUploadUrl(String bucket, String objectKey, int expirySeconds) throws Exception {

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build();

                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                .signatureDuration(Duration.ofSeconds(expirySeconds))
                                .putObjectRequest(putObjectRequest)
                                .build();

                PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

                return presignedRequest.url().toString();
        }

        @Override
        public String getPresignedUrl(String bucket, String objectKey, int expirySeconds) throws Exception {

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                                .signatureDuration(Duration.ofSeconds(expirySeconds))
                                .getObjectRequest(getObjectRequest)
                                .build();

                PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

                return presignedRequest.url().toString();
        }

        // ========================
        // COPY
        // ========================

        @Override
        public void copy(String sourceBucket, String sourceKey, String targetBucket, String targetKey)
                        throws Exception {

                CopyObjectRequest request = CopyObjectRequest.builder()
                                .sourceBucket(sourceBucket)
                                .sourceKey(sourceKey)
                                .destinationBucket(targetBucket)
                                .destinationKey(targetKey)
                                .build();

                s3Client.copyObject(request);
        }

        // ========================
        // MULTIPART UPLOAD
        // ========================

        @Override
        public String initiateMultipartUpload(String bucket, String objectKey) throws Exception {

                CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build();

                CreateMultipartUploadResponse response = s3Client.createMultipartUpload(request);

                return response.uploadId();
        }

        @Override
        public String getPresignedUploadPartUrl(String bucket, String objectKey, String uploadId,
                        int partNumber, int expirySeconds) throws Exception {

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .uploadId(uploadId)
                                .partNumber(partNumber)
                                .build();

                UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                                .signatureDuration(Duration.ofSeconds(expirySeconds))
                                .uploadPartRequest(uploadPartRequest)
                                .build();

                PresignedUploadPartRequest presignedRequest = s3Presigner.presignUploadPart(presignRequest);

                return presignedRequest.url().toString();
        }

        @Override
        public void completeMultipartUpload(String bucket, String objectKey, String uploadId,
                        List<PartETag> parts) throws Exception {

                List<CompletedPart> completedParts = parts.stream()
                                .map((PartETag p) -> CompletedPart.builder()
                                                .partNumber(p.getPartNumber())
                                                .eTag(p.getETag())
                                                .build())
                                .collect(Collectors.toList());

                CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                                .parts(completedParts)
                                .build();

                CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .uploadId(uploadId)
                                .multipartUpload(completedMultipartUpload)
                                .build();

                s3Client.completeMultipartUpload(request);
        }

        @Override
        public void abortMultipartUpload(String bucket, String objectKey, String uploadId) throws Exception {

                AbortMultipartUploadRequest request = AbortMultipartUploadRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .uploadId(uploadId)
                                .build();

                s3Client.abortMultipartUpload(request);
        }
}