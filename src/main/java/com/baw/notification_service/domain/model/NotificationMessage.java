package com.baw.notification_service.domain.model;

public record NotificationMessage(
        String recipient,
        String subject,
        String body
) {
}
