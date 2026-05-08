package com.connector.application.result;

import java.time.LocalDateTime;

public record EmailTemplateResult(
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
