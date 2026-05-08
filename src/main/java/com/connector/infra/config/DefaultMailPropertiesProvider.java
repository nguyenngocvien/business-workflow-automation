package com.connector.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.connector.infra.mail.MailProperties;
import com.connector.infra.mail.MailPropertiesProvider;

@Component
public class DefaultMailPropertiesProvider implements MailPropertiesProvider {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Override
    public MailProperties get() {
        MailProperties p = new MailProperties();
        p.setHost(host);
        p.setPort(port);
        p.setUsername(username);
        p.setPassword(password);
        return p;
    }
}