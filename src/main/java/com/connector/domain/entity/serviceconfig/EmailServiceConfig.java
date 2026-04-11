package com.connector.domain.entity.serviceconfig;

import java.util.List;
import java.util.Map;

import com.connector.domain.enums.MailProtocolType;

public record EmailServiceConfig(
    String host,
    Integer port,
    String username,
    String password,
    String from,
    MailProtocolType protocol,
    Boolean auth,
    Boolean starttls,
    Boolean ssl,
    String templateType,
    String templateCode,
    String subject,
    String body,
    Boolean html,
    List<String> to,
    List<String> cc,
    List<String> bcc,
    Map<String, String> properties
) implements ServiceConfig {
}
