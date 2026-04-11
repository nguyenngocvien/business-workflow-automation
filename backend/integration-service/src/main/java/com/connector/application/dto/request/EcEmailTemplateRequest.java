package com.connector.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EcEmailTemplateRequest(
    @NotBlank(message = "appId is required")
    @Size(max = 100, message = "appId must be at most 100 characters")
    String appId,
    @NotBlank(message = "templateType is required")
    @Size(max = 50, message = "templateType must be at most 50 characters")
    String templateType,
    @NotBlank(message = "templateCode is required")
    @Size(max = 100, message = "templateCode must be at most 100 characters")
    String templateCode,
    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    String title,
    @NotBlank(message = "content is required")
    String content,
    @NotNull(message = "status is required")
    Boolean status,
    @Size(max = 100, message = "createdBy must be at most 100 characters")
    String createdBy,
    @Size(max = 100, message = "updatedBy must be at most 100 characters")
    String updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
