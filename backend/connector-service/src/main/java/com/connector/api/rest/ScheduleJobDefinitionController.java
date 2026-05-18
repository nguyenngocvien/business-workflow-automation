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

import com.connector.application.command.CreateScheduleJobCommand;
import com.connector.application.command.UpdateScheduleJobCommand;
import com.connector.application.dto.CreateScheduleJobRequest;
import com.connector.application.dto.UpdateScheduleJobRequest;
import com.connector.application.result.ScheduleJobResult;
import com.connector.application.usecase.ScheduleJobDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/schedule-jobs")
@Tag(name = "Schedule Jobs", description = "Manage scheduled job definitions")
public class ScheduleJobDefinitionController {

    private final ScheduleJobDefinitionUseCase useCase;

    public ScheduleJobDefinitionController(ScheduleJobDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List scheduled jobs")
    public Page<ScheduleJobResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a scheduled job by id")
    public ScheduleJobResult findById(@Parameter(description = "Scheduled job id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a scheduled job")
    public ResponseEntity<ScheduleJobResult> create(@Valid @RequestBody CreateScheduleJobRequest request) {
        CreateScheduleJobCommand cmd = new CreateScheduleJobCommand(
            request.jobCode(),
            request.jobName(),
            request.jobType(),
            request.serviceId(),
            request.pipelineId(),
            request.cronExpression(),
            request.fixedRateMs(),
            request.enabled()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(cmd));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a scheduled job")
    public ScheduleJobResult update(
        @Parameter(description = "Scheduled job id") @PathVariable Long id,
        @Valid @RequestBody UpdateScheduleJobRequest request
    ) {
        UpdateScheduleJobCommand cmd = new UpdateScheduleJobCommand(
            id,
            request.jobName(),
            request.jobType(),
            request.serviceId(),
            request.pipelineId(),
            request.cronExpression(),
            request.fixedRateMs(),
            request.enabled()
        );
        return useCase.update(cmd);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a scheduled job")
    public ResponseEntity<Void> delete(@Parameter(description = "Scheduled job id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
