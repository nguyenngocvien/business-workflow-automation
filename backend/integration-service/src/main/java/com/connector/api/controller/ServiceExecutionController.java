package com.connector.api.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.application.service.ServiceExecutionApplicationService;

@RestController
@RequestMapping("/api/execute")
public class ServiceExecutionController {

    private final ServiceExecutionApplicationService serviceExecutionApplicationService;

    public ServiceExecutionController(ServiceExecutionApplicationService serviceExecutionApplicationService) {
        this.serviceExecutionApplicationService = serviceExecutionApplicationService;
    }

    @PostMapping("/{appId}/{serviceCode}/{serviceVersion}")
    public ResponseEntity<ExecuteServiceResponse> execute(
        @PathVariable String appId,
        @PathVariable String serviceCode,
        @PathVariable String serviceVersion,
        @Valid @RequestBody(required = false) ExecuteServiceRequest request
    ) {
        return ResponseEntity.ok(
            serviceExecutionApplicationService.execute(appId, serviceCode, serviceVersion, request)
        );
    }
}
