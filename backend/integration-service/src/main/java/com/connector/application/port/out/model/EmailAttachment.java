package com.connector.application.port.out.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAttachment {

    private String filename;
    private byte[] content;
    private String contentType;
}