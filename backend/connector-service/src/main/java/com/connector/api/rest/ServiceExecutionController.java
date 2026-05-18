package com.connector.api.rest;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.dto.ExecuteServiceRequest;
import com.connector.application.result.ExecuteServiceResponse;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.application.usecase.ServiceExecutionUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/execute")
@Tag(name = "Service Execution", description = "Execute a service definition")
public class ServiceExecutionController {

    private final ServiceExecutionUseCase useCase;

    public ServiceExecutionController(ServiceExecutionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/{appId}/{serviceCode}/{serviceVersion}")
    @Operation(summary = "Execute a service")
    public ResponseEntity<ExecuteServiceResponse> execute(
        @Parameter(description = "Application id") @PathVariable String appId,
        @Parameter(description = "Service code") @PathVariable String serviceCode,
        @Parameter(description = "Service version") @PathVariable String serviceVersion,
        @Valid @RequestBody(required = false) ExecuteServiceRequest request
    ) {
        ExecuteServiceCommand command = new ExecuteServiceCommand(appId, serviceCode, serviceVersion, request.headers(), request.payload());
        ExecuteServiceResult result = useCase.execute(command);
        return ResponseEntity.ok(result.toResponseDto());
    }
}
