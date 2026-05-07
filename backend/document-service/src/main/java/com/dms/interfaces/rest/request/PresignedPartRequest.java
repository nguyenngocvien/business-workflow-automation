package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PresignedPartRequest {
    private String bucket;
    private String objectKey;
    private String uploadId;
    private int partNumber;
}