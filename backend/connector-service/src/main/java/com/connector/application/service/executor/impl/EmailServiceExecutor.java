package com.connector.application.service.executor.impl;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.port.out.EmailSender;
import com.connector.application.port.out.model.EmailMessage;
import com.connector.application.service.executor.AbstractServiceExecutor;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailServiceExecutor extends AbstractServiceExecutor {

    private final EmailSender emailSender;

    public EmailServiceExecutor(ObjectMapper mapper, EmailSender emailSender) {
        super(mapper);
        this.emailSender = emailSender;
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.EMAIL;
    }

    @Override
    protected Object prepare(ServiceEntity service, JsonNode config, ExecuteServiceCommand request) {

        EmailMessage msg = new EmailMessage();
        msg.setTo(List.of(text(config, "to")));
        msg.setSubject(text(config, "subject"));
        msg.setBody(text(config, "body"));

        return msg;
    }

    @Override
    protected Object invoke(Object prepared) {
        emailSender.send((EmailMessage) prepared);
        return "OK";
    }

    @Override
    protected JsonNode buildBody(Object result) {
        return objectMapper.createObjectNode()
            .put("message", "Email sent");
    }
}