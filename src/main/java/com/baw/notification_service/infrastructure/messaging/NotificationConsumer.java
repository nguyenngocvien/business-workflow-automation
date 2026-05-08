package com.baw.notification_service.infrastructure.messaging;

import com.baw.notification_service.application.port.in.NotificationUseCase;
import com.baw.notification_service.domain.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationUseCase notificationUseCase;

    public NotificationConsumer(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @RabbitListener(queues = "${notification.rabbitmq.queue}")
    public void consume(NotificationMessage message) {
        log.info("Received notification message for recipient={}", message.recipient());
        notificationUseCase.process(message);
    }
}
