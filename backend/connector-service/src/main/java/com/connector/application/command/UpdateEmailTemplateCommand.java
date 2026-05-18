package com.connector.application.command;

public record UpdateEmailTemplateCommand(

    Long id,

    String templateType,
    String title,
    String content,
    Boolean status,
    String updatedBy

) {}