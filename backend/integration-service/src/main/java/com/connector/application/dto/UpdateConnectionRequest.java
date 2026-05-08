package com.connector.application.dto;

import java.util.Map;

import com.connector.domain.enums.ConnectionType;

public record UpdateConnectionRequest(

    String connectionName,

    ConnectionType connectionType,

    Map<String, Object> config,

    Boolean active

) {}