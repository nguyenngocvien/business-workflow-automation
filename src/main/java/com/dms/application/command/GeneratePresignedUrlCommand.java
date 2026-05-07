package com.dms.application.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePresignedUrlCommand {
    private String bucket;
    private String objectKey;
    private String uploadId;
    private int totalParts;
}