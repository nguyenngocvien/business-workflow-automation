package com.connector.infra.config;

import com.connector.application.port.out.EmailSender;
import com.connector.infra.mail.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Bean
    public EmailSender emailSender(MailPropertiesProvider provider) {
        return new JavaMailEmailSender(provider);
    }
}