package com.dms.application.result;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileSearchOptionsResult {

    private List<String> categories;
    private List<AttributeOptionDto> attributes;

    @Getter
    @AllArgsConstructor
    public static class AttributeOptionDto {
        private String key;
        private String displayName;
        private String dataType;
    }
}