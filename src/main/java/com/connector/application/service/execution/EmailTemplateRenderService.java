package com.connector.application.service.execution;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcEmailTemplate;
import com.connector.domain.entity.EcService;
import com.connector.domain.repository.EcEmailTemplateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailTemplateRenderService {

    private final EcEmailTemplateRepository emailTemplateRepository;
    private final ObjectMapper objectMapper;

    public EmailTemplateRenderService(
        EcEmailTemplateRepository emailTemplateRepository,
        ObjectMapper objectMapper
    ) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.objectMapper = objectMapper;
    }

    public RenderedEmailTemplate resolve(EcService service, JsonNode config, JsonNode payload) {
        String templateCode = firstNonBlank(text(payload, "templateCode"), text(config, "templateCode"));
        if (!StringUtils.hasText(templateCode)) {
            return RenderedEmailTemplate.empty();
        }

        String templateType = firstNonBlank(
            text(payload, "templateType"),
            text(config, "templateType"),
            "EMAIL"
        );

        EcEmailTemplate template = emailTemplateRepository
            .findByAppIdAndTemplateTypeAndTemplateCode(service.getAppId(), templateType, templateCode)
            .filter(item -> Boolean.TRUE.equals(item.getStatus()))
            .or(() -> emailTemplateRepository.findByAppIdAndTemplateCode(service.getAppId(), templateCode)
                .filter(item -> Boolean.TRUE.equals(item.getStatus())))
            .orElseThrow(() -> new ResourceNotFoundException(
                "EcEmailTemplate not found with appId=%s, templateType=%s, templateCode=%s"
                    .formatted(service.getAppId(), templateType, templateCode)
            ));

        JsonNode variables = resolveTemplateVariables(payload);
        return new RenderedEmailTemplate(
            renderTemplate(template.getTitle(), variables),
            renderTemplate(template.getContent(), variables)
        );
    }

    private JsonNode resolveTemplateVariables(JsonNode payload) {
        if (payload == null || payload.isNull()) {
            return objectMapper.createObjectNode();
        }
        JsonNode variables = payload.get("variables");
        if (variables != null && variables.isObject()) {
            return variables;
        }
        return payload;
    }

    private String renderTemplate(String template, JsonNode variables) {
        if (!StringUtils.hasText(template) || variables == null || variables.isNull()) {
            return template;
        }
        String[] rendered = {template};
        if (variables.isObject()) {
            variables.fields().forEachRemaining(entry -> {
                String placeholder = "${" + entry.getKey() + "}";
                rendered[0] = rendered[0].replace(placeholder, stringifyTemplateValue(entry.getValue()));
            });
        }
        return rendered[0];
    }

    private String stringifyTemplateValue(JsonNode value) {
        if (value == null || value.isNull()) {
            return "";
        }
        if (value.isValueNode()) {
            return value.asText();
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return value.toString();
        }
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(fieldName);
        return value != null ? value.asText() : null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    public record RenderedEmailTemplate(String subject, String content) {
        public static RenderedEmailTemplate empty() {
            return new RenderedEmailTemplate(null, null);
        }
    }
}
