package com.connector.api.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.command.CreateConnectionCommand;
import com.connector.application.command.UpdateConnectionCommand;
import com.connector.application.dto.CreateConnectionRequest;
import com.connector.application.dto.UpdateConnectionRequest;
import com.connector.application.result.ConnectionResult;
import com.connector.application.usecase.ConnectionDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/connection-definitions")
@Tag(name = "Connection Definitions", description = "Manage connector definitions")
public class ConnectionDefinitionController {

    private final ConnectionDefinitionUseCase useCase;

    public ConnectionDefinitionController(ConnectionDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List connection definitions")
    public Page<ConnectionResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a connection definition by id")
    public ConnectionResult findById(@Parameter(description = "Connection definition id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a connection definition")
    public ResponseEntity<ConnectionResult> create(@Valid @RequestBody CreateConnectionRequest request) {
        CreateConnectionCommand command = new CreateConnectionCommand(
            request.connectionCode(),
            request.connectionName(),
            request.connectionType(),
            request.config(),
            request.active()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(command));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a connection definition")
    public ConnectionResult update(
        @Parameter(description = "Connection definition id") @PathVariable Long id,
        @Valid @RequestBody UpdateConnectionRequest request
    ) {
        UpdateConnectionCommand command = new UpdateConnectionCommand(
            id,
            request.connectionName(),
            request.connectionType(),
            request.config(),
            request.active()
        );
        return useCase.update(command);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a connection definition")
    public ResponseEntity<Void> delete(@Parameter(description = "Connection definition id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
