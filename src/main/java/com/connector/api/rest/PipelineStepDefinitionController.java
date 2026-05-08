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

import com.connector.application.command.CreatePipelineStepCommand;
import com.connector.application.command.UpdatePipelineStepCommand;
import com.connector.application.dto.CreatePipelineStepRequest;
import com.connector.application.dto.UpdatePipelineStepRequest;
import com.connector.application.result.PipelineStepResult;
import com.connector.application.usecase.PipelineStepDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/pipeline-steps")
@Tag(name = "Pipeline Steps", description = "Manage pipeline step definitions")
public class PipelineStepDefinitionController {

    private final PipelineStepDefinitionUseCase useCase;

    public PipelineStepDefinitionController(PipelineStepDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List pipeline steps")
    public Page<PipelineStepResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pipeline step by id")
    public PipelineStepResult findById(@Parameter(description = "Pipeline step id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a pipeline step")
    public ResponseEntity<PipelineStepResult> create(@Valid @RequestBody CreatePipelineStepRequest request) {
        CreatePipelineStepCommand command = new CreatePipelineStepCommand(
            request.pipelineId(),
            request.stepOrder(),
            request.serviceId(),
            request.stepName(),
            request.requestTransform(),
            request.responseTransform(),
            request.continueOnError()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(command));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pipeline step")
    public PipelineStepResult update(
        @Parameter(description = "Pipeline step id") @PathVariable Long id,
        @Valid @RequestBody UpdatePipelineStepRequest request
    ) {
        UpdatePipelineStepCommand command = new UpdatePipelineStepCommand(
            id,
            request.stepOrder(),
            request.serviceId(),
            request.stepName(),
            request.requestTransform(),
            request.responseTransform(),
            request.continueOnError()
        );
        return useCase.update(command);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pipeline step")
    public ResponseEntity<Void> delete(@Parameter(description = "Pipeline step id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
