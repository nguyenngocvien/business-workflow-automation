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

import com.connector.application.command.CreateServiceCommand;
import com.connector.application.command.UpdateServiceCommand;
import com.connector.application.dto.CreateServiceRequest;
import com.connector.application.dto.UpdateServiceRequest;
import com.connector.application.result.ServiceResult;
import com.connector.application.usecase.ServiceDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/service-definitions")
@Tag(name = "Service Definitions", description = "Manage service definitions")
public class ServiceDefinitionController {

    private final ServiceDefinitionUseCase useCase;

    public ServiceDefinitionController(ServiceDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List service definitions")
    public Page<ServiceResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a service definition by id")
    public ServiceResult findById(@Parameter(description = "Service definition id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a service definition")
    public ResponseEntity<ServiceResult> create(@Valid @RequestBody CreateServiceRequest request) {
        CreateServiceCommand cmd = new CreateServiceCommand(
            request.serviceCode(),
            request.serviceName(),
            request.serviceType(),
            request.serviceVersion(),
            request.appId(),
            request.connectionId(),
            request.config(),
            request.timeoutMs(),
            request.retryCount(),
            request.active(),
            request.logEnable(),
            request.createdBy()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(cmd));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service definition")
    public ServiceResult update(
        @Parameter(description = "Service definition id") @PathVariable Long id,
        @Valid @RequestBody UpdateServiceRequest request
    ) {
        UpdateServiceCommand cmd = new UpdateServiceCommand(
            id,
            request.serviceName(),
            request.serviceType(),
            request.serviceVersion(),
            request.appId(),
            request.connectionId(),
            request.config(),
            request.timeoutMs(),
            request.retryCount(),
            request.active(),
            request.logEnable(),
            request.updatedBy()
        );
        return useCase.update(cmd);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service definition")
    public ResponseEntity<Void> delete(@Parameter(description = "Service definition id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
