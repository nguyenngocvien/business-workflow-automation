package com.dms.application.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateUploadCommand {
    private String bucket;
    private String fileName;
    private String refType;
    private String refId;
}