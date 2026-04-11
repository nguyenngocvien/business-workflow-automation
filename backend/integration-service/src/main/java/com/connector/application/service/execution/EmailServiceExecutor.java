package com.connector.application.service.execution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.application.service.execution.EmailTemplateRenderService.RenderedEmailTemplate;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailServiceExecutor implements TypedServiceExecutor {

    private final ObjectMapper objectMapper;
    private final EmailTemplateRenderService emailTemplateRenderService;

    public EmailServiceExecutor(
        ObjectMapper objectMapper,
        EmailTemplateRenderService emailTemplateRenderService
    ) {
        this.objectMapper = objectMapper;
        this.emailTemplateRenderService = emailTemplateRenderService;
    }

    @Override
    public ServiceType supportedType() {
        return ServiceType.EMAIL;
    }

    @Override
    public ExecuteServiceResponse execute(EcService service, ExecuteServiceRequest request) {
        JsonNode config = parseConfig(service.getConfigJson());
        JsonNode payload = request != null ? request.payload() : null;

        try {
            JavaMailSenderImpl mailSender = createMailSender(config);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            RenderedEmailTemplate renderedTemplate = emailTemplateRenderService.resolve(service, config, payload);
            String from = firstText(payload, "from", text(config, "from"));
            String subject = firstNonBlank(renderedTemplate.subject(), firstText(payload, "subject", text(config, "subject")));
            String body = firstNonBlank(renderedTemplate.content(), firstText(payload, "body", text(config, "body")));
            boolean html = booleanValue(payload, "html", booleanValue(config, "html", false));

            if (!StringUtils.hasText(subject)) {
                throw new IllegalArgumentException("Email subject is required");
            }
            if (!StringUtils.hasText(body)) {
                throw new IllegalArgumentException("Email body is required");
            }

            List<String> to = recipients(payload, config, "to");
            if (to.isEmpty()) {
                throw new IllegalArgumentException("At least one email recipient is required");
            }
            List<String> cc = recipients(payload, config, "cc");
            List<String> bcc = recipients(payload, config, "bcc");
            validateRecipients(to, "to");
            validateRecipients(cc, "cc");
            validateRecipients(bcc, "bcc");

            helper.setTo(to.toArray(String[]::new));
            setRecipients(helper, cc, RecipientType.CC);
            setRecipients(helper, bcc, RecipientType.BCC);
            if (StringUtils.hasText(from)) {
                validateEmail(from, "from");
                helper.setFrom(from);
            }
            helper.setSubject(subject);
            helper.setText(body, html);
            addAttachments(helper, payload);
            mailSender.send(message);

            JsonNode responseBody = objectMapper.createObjectNode()
                .put("message", "Email sent successfully")
                .put("subject", subject)
                .put("recipientCount", to.size());

            return new ExecuteServiceResponse(
                service.getId(),
                service.getAppId(),
                service.getServiceCode(),
                service.getServiceVersion(),
                service.getServiceType(),
                200,
                Collections.emptyMap(),
                responseBody
            );
        } catch (Exception ex) {
            throw new ServiceExecutionException(
                "Email execution failed: " + ex.getMessage(),
                ex,
                HttpStatusCode.valueOf(500),
                objectMapper.getNodeFactory().textNode(ex.getMessage())
            );
        }
    }

    private JavaMailSenderImpl createMailSender(JsonNode config) {
        String host = text(config, "host");
        if (!StringUtils.hasText(host)) {
            throw new IllegalArgumentException("configJson.host is required for EMAIL service execution");
        }

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(intValue(config, "port", 587));
        sender.setUsername(text(config, "username"));
        sender.setPassword(text(config, "password"));
        sender.setProtocol(resolveMailProtocol(config));
        sender.setDefaultEncoding("UTF-8");

        Properties properties = sender.getJavaMailProperties();
        properties.put("mail.transport.protocol", sender.getProtocol());
        properties.put("mail.smtp.auth", String.valueOf(booleanValue(config, "auth", true)));
        properties.put("mail.smtp.starttls.enable", String.valueOf(booleanValue(config, "starttls", true)));
        properties.put("mail.smtp.ssl.enable", String.valueOf(booleanValue(config, "ssl", false)));

        JsonNode propsNode = config.get("properties");
        if (propsNode != null && propsNode.isObject()) {
            propsNode.fields().forEachRemaining(entry -> properties.put(entry.getKey(), entry.getValue().asText()));
        }
        return sender;
    }

    private List<String> recipients(JsonNode payload, JsonNode config, String fieldName) {
        JsonNode node = payload != null && payload.has(fieldName) ? payload.get(fieldName) : config.get(fieldName);
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            node.forEach(item -> {
                if (item != null && StringUtils.hasText(item.asText())) {
                    values.add(item.asText());
                }
            });
            return values;
        }
        String raw = node.asText();
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .toList();
    }

    private void validateRecipients(List<String> recipients, String fieldName) {
        for (String recipient : recipients) {
            validateEmail(recipient, fieldName);
        }
    }

    private void validateEmail(String email, String fieldName) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Invalid email address in " + fieldName + ": " + email);
        }
    }

    private void addAttachments(MimeMessageHelper helper, JsonNode payload) throws Exception {
        if (payload == null) {
            return;
        }
        JsonNode attachments = payload.get("attachments");
        if (attachments == null || !attachments.isArray()) {
            return;
        }
        for (JsonNode attachment : attachments) {
            String filename = text(attachment, "filename");
            String base64 = firstNonBlank(text(attachment, "base64"), text(attachment, "contentBase64"), text(attachment, "content"));
            if (!StringUtils.hasText(filename)) {
                throw new IllegalArgumentException("Attachment filename is required");
            }
            if (!StringUtils.hasText(base64)) {
                throw new IllegalArgumentException("Attachment base64 content is required for file: " + filename);
            }

            byte[] content;
            try {
                content = Base64.getDecoder().decode(base64);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Attachment content is not valid base64 for file: " + filename);
            }

            String contentType = firstText(attachment, "contentType", MediaType.APPLICATION_OCTET_STREAM_VALUE);
            helper.addAttachment(filename, new ByteArrayResource(content), contentType);
        }
    }

    private void setRecipients(MimeMessageHelper helper, List<String> recipients, RecipientType type) throws Exception {
        if (recipients.isEmpty()) {
            return;
        }
        String[] values = recipients.toArray(String[]::new);
        if (type == RecipientType.CC) {
            helper.setCc(values);
            return;
        }
        helper.setBcc(values);
    }

    private JsonNode parseConfig(String configJson) {
        try {
            return objectMapper.readTree(configJson);
        } catch (Exception ex) {
            throw new IllegalArgumentException("configJson is not valid JSON");
        }
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(fieldName);
        return value != null ? value.asText() : null;
    }

    private String firstText(JsonNode node, String fieldName, String fallback) {
        if (node == null) {
            return fallback;
        }
        String value = text(node, fieldName);
        return StringUtils.hasText(value) ? value : fallback;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private int intValue(JsonNode node, String fieldName, int defaultValue) {
        if (node == null) {
            return defaultValue;
        }
        JsonNode value = node.get(fieldName);
        return value != null && value.canConvertToInt() ? value.asInt() : defaultValue;
    }

    private boolean booleanValue(JsonNode node, String fieldName, boolean defaultValue) {
        JsonNode value = node != null ? node.get(fieldName) : null;
        return value != null ? value.asBoolean() : defaultValue;
    }

    private String resolveMailProtocol(JsonNode config) {
        String protocol = firstText(config, "protocol", "SMTP");
        return protocol.toLowerCase();
    }

    private enum RecipientType {
        CC,
        BCC
    }
}
