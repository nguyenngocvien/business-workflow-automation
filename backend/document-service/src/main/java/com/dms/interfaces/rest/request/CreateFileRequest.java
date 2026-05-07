package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFileRequest {

    private String fileName;
    private String contentType;
    private String categoryCode;
}