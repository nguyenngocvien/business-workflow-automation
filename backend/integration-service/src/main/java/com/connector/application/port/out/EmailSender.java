package com.connector.application.port.out;

import com.connector.application.port.out.model.EmailMessage;

public interface EmailSender {

    void send(EmailMessage message);

}