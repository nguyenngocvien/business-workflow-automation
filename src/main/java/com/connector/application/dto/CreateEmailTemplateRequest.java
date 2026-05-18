package com.connector.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEmailTemplateRequest(

    @NotBlank(message = "appId is required")
    @Size(max = 100)
    String appId,

    @NotBlank(message = "templateType is required")
    @Size(max = 50)
    String templateType,

    @NotBlank(message = "templateCode is required")
    @Size(max = 100)
    String templateCode,

    @NotBlank(message = "title is required")
    @Size(max = 255)
    String title,

    @NotBlank(message = "content is required")
    String content,

    @NotNull(message = "status is required")
    Boolean status,

    @Size(max = 100)
    String createdBy

) {}