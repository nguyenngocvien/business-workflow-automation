package com.dms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteUploadCommand {
    private String objectKey;
    private String fileName;
    private Long size;
    private String contentType;
    private String categoryCode;
}