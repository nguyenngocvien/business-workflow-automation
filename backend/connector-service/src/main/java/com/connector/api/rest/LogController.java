package com.connector.api.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.result.LogResult;
import com.connector.application.usecase.LogUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Logs", description = "Inspect and delete log records")
public class LogController {

    private final LogUseCase useCase;

    public LogController(LogUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List logs")
    public Page<LogResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a log entry")
    public LogResult findById(
        @Parameter(description = "Log id") @PathVariable Long id
    ) {
        return useCase.findById(id);
    }
}
