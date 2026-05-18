package com.connector.application.port.out.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {

    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;

    private String subject;
    private String body;
    private boolean html;

    private List<EmailAttachment> attachments;
}