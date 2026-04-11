package com.connector.domain.entity.connectionconfig;

import com.connector.domain.enums.MailProtocolType;

public record SmtpConnectionConfig(
    String host,
    Integer port,
    String username,
    String password,
    MailProtocolType protocol,
    Boolean auth,
    Boolean starttls,
    Boolean ssl,
    String from
) implements ConnectionConfig {
}
