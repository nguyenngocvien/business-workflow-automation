package com.dms.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitiateMultipartResult {
    private String uploadId;
    private String objectKey;
    private String bucket;
}