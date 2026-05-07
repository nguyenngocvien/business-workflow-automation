package com.dms.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUploadResult {

    private String uploadUrl;
    private String objectKey;
    private String bucket;
}