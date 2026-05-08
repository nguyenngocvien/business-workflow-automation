package com.connector.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateEmailTemplateRequest(

    @Size(max = 50)
    String templateType,

    @Size(max = 255)
    String title,

    String content,

    Boolean status,

    @Size(max = 100)
    String updatedBy

) {}