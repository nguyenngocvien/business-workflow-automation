package com.dms.application.command;

import com.dms.domain.enums.AttributeDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateFileAttributeCommand {

    private final String keyCode;
    private final String displayName;
    private final AttributeDataType dataType;
    private final Boolean isRequired;
}