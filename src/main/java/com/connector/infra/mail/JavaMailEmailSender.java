package com.connector.infra.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.*;

import com.connector.application.port.out.EmailSender;
import com.connector.application.port.out.model.EmailAttachment;
import com.connector.application.port.out.model.EmailMessage;

import java.util.Properties;

public class JavaMailEmailSender implements EmailSender {

    private final MailPropertiesProvider propertiesProvider;

    public JavaMailEmailSender(MailPropertiesProvider propertiesProvider) {
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    public void send(EmailMessage message) {

        JavaMailSenderImpl sender = buildSender();

        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(message.getTo().toArray(String[]::new));

            if (message.getCc() != null) {
                helper.setCc(message.getCc().toArray(String[]::new));
            }

            if (message.getBcc() != null) {
                helper.setBcc(message.getBcc().toArray(String[]::new));
            }

            helper.setSubject(message.getSubject());
            helper.setText(message.getBody(), message.isHtml());

            if (message.getFrom() != null) {
                helper.setFrom(message.getFrom());
            }

            if (message.getAttachments() != null) {
                for (EmailAttachment att : message.getAttachments()) {
                    helper.addAttachment(
                        att.getFilename(),
                        new ByteArrayResource(att.getContent()),
                        att.getContentType()
                    );
                }
            }

            sender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Send email failed", e);
        }
    }

    private JavaMailSenderImpl buildSender() {
        var config = propertiesProvider.get();

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getHost());
        sender.setPort(config.getPort());
        sender.setUsername(config.getUsername());
        sender.setPassword(config.getPassword());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", config.isAuth());
        props.put("mail.smtp.starttls.enable", config.isStarttls());

        return sender;
    }
}