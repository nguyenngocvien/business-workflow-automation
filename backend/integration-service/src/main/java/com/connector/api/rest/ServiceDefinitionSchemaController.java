package com.connector.api.rest;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.usecase.ServiceDefinitionUseCase;
import com.connector.domain.enums.ServiceType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/service-definition-schemas")
@Tag(name = "Service Schemas", description = "Inspect service definition schemas")
public class ServiceDefinitionSchemaController {

    private final ServiceDefinitionUseCase useCase;

    public ServiceDefinitionSchemaController(ServiceDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{serviceType}")
    @Operation(summary = "Get a service definition schema")
    public Map<String, Object> getSchema(
        @Parameter(description = "Service type") @PathVariable ServiceType serviceType
    ) {
        return useCase.schema(serviceType);
    }
}
