package com.connector.application.command;

public record CreateEmailTemplateCommand(

    String appId,
    String templateType,
    String templateCode,
    String title,
    String content,
    Boolean status,
    String createdBy

) {}