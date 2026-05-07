package com.dms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUploadCommand {
    private String fileName;
    private String contentType;
    private String categoryCode;
}