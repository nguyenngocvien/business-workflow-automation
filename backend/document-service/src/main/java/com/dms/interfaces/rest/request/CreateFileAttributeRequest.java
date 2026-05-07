package com.dms.interfaces.rest.request;

import com.dms.domain.enums.AttributeDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFileAttributeRequest {

    private String keyCode;
    private String displayName;
    private AttributeDataType dataType;
    private Boolean isRequired;
}