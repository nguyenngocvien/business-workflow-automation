package com.baw.notification_service.application.service;

import com.baw.notification_service.application.port.in.NotificationUseCase;
import com.baw.notification_service.domain.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationUseCaseImpl implements NotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificationUseCaseImpl.class);

    @Override
    public void process(NotificationMessage message) {
        log.info("Processing notification for recipient={}, subject={}",
                message.recipient(), message.subject());

        // Replace this with mail/SMS/push delivery logic as the service grows.
        log.debug("Notification body: {}", message.body());
    }
}
