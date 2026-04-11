package com.connector.api.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.service.config.ConnectionConfigSupportService;
import com.connector.domain.enums.ConnectionType;

@RestController
@RequestMapping("/api/connection-config-schemas")
public class ConnectionConfigSchemaController {

    private final ConnectionConfigSupportService connectionConfigSupportService;

    public ConnectionConfigSchemaController(ConnectionConfigSupportService connectionConfigSupportService) {
        this.connectionConfigSupportService = connectionConfigSupportService;
    }

    @GetMapping("/{connectionType}")
    public Map<String, Object> getSchema(@PathVariable ConnectionType connectionType) {
        return connectionConfigSupportService.schema(connectionType);
    }
}
