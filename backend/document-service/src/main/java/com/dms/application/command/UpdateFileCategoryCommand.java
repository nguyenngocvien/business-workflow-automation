package com.dms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateFileCategoryCommand {

    private final Long id;
    private final String name;
    private final Long maxSize;
    private final String bucketName;
    private final Integer retentionDays;
    private final Boolean isPublic;
}