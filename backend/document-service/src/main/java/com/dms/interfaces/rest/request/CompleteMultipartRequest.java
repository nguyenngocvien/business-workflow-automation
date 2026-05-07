package com.dms.interfaces.rest.request;

import java.util.List;

import com.dms.application.port.out.PartETag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteMultipartRequest {
    private String objectKey;
    private String uploadId;
    private List<PartETag> parts;
    private String fileName;
    private String categoryCode;
    private String contentType;
    private Long size;
}