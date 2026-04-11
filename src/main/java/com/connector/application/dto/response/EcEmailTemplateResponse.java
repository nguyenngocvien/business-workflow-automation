package com.connector.application.dto.response;

import java.time.LocalDateTime;

public record EcEmailTemplateResponse(
    Long id,
    String appId,
    String templateType,
    String templateCode,
    String title,
    String content,
    Boolean status,
    String createdBy,
    String updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
