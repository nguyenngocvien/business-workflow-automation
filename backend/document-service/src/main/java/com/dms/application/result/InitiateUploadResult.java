package com.dms.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitiateUploadResult {
    private String objectKey;
    private String uploadId;
}