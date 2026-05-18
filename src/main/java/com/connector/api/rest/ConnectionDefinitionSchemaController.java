package com.connector.api.rest;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.usecase.ConnectionDefinitionUseCase;
import com.connector.domain.enums.ConnectionType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/connection-definition-schemas")
@Tag(name = "Connection Schemas", description = "Inspect connection configuration schemas")
public class ConnectionDefinitionSchemaController {

    private final ConnectionDefinitionUseCase useCase;

    public ConnectionDefinitionSchemaController(ConnectionDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{connectionType}")
    @Operation(summary = "Get a connection configuration schema")
    public Map<String, Object> getSchema(
        @Parameter(description = "Connection type") @PathVariable ConnectionType connectionType
    ) {
        return useCase.schema(connectionType);
    }
}
