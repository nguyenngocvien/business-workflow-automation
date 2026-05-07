package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFileCategoryRequest {

    private String name;
    private Long maxSize;
    private String bucketName;
    private Integer retentionDays;
    private Boolean isPublic;
}