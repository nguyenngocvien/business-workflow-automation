package com.dms.application.result;

import com.dms.domain.enums.AttributeDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileAttributeResult {

    private Long id;
    private String keyCode;
    private String displayName;
    private AttributeDataType dataType;
    private Boolean isRequired;
}