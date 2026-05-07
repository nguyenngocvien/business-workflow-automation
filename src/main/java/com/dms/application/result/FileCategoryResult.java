package com.dms.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileCategoryResult {

    private Long id;
    private String code;
    private String name;
    private Boolean isPublic;
}