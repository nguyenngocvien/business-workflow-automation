package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteUploadRequest {

    private String objectKey;
    private String fileName;
    private Long size;
    private String contentType;
    private String categoryCode;
}