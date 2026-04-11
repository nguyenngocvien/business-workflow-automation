package com.connector.domain.entity.connectionconfig;

import java.util.Map;

public record SoapConnectionConfig(
    String wsdlUrl,
    String endpoint,
    String soapAction,
    Map<String, String> headers
) implements ConnectionConfig {
}
