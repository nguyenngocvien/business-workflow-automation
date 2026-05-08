package com.baw.notification_service.application.port.in;

import com.baw.notification_service.domain.model.NotificationMessage;

public interface NotificationUseCase {

    void process(NotificationMessage message);
}
