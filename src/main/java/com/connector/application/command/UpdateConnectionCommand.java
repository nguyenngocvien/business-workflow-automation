package com.connector.application.command;

import java.util.Map;

import com.connector.domain.enums.ConnectionType;

public record UpdateConnectionCommand(

    Long id,

    String connectionName,
    ConnectionType connectionType,
    Map<String, Object> config,
    Boolean active

) {}