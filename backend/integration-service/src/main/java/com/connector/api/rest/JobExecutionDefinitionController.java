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

import com.connector.application.command.CreateJobExecutionCommand;
import com.connector.application.command.UpdateJobExecutionCommand;
import com.connector.application.dto.CreateJobExecutionRequest;
import com.connector.application.dto.UpdateJobExecutionRequest;
import com.connector.application.result.JobExecutionResult;
import com.connector.application.usecase.JobExecutionDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/job-executions")
@Tag(name = "Job Executions", description = "Manage job execution definitions")
public class JobExecutionDefinitionController {

    private final JobExecutionDefinitionUseCase useCase;

    public JobExecutionDefinitionController(JobExecutionDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List job executions")
    public Page<JobExecutionResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job execution by id")
    public JobExecutionResult findById(@Parameter(description = "Job execution id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a job execution")
    public ResponseEntity<JobExecutionResult> create(@Valid @RequestBody CreateJobExecutionRequest request) {
        CreateJobExecutionCommand cmd = new CreateJobExecutionCommand(
            request.jobId(),
            request.startTime(),
            request.requestData()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(cmd));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job execution")
    public JobExecutionResult update(
        @Parameter(description = "Job execution id") @PathVariable Long id,
        @Valid @RequestBody UpdateJobExecutionRequest request
    ) {
        UpdateJobExecutionCommand cmd = new UpdateJobExecutionCommand(
            id,
            request.endTime(),
            request.status(),
            request.responseData(),
            request.errorMessage()
        );
        return useCase.update(cmd);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job execution")
    public ResponseEntity<Void> delete(@Parameter(description = "Job execution id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
