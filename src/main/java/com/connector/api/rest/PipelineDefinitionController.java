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

import com.connector.application.command.CreatePipelineCommand;
import com.connector.application.command.UpdatePipelineCommand;
import com.connector.application.dto.CreatePipelineRequest;
import com.connector.application.dto.UpdatePipelineRequest;
import com.connector.application.result.PipelineResult;
import com.connector.application.usecase.PipelineDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/pipelines")
@Tag(name = "Pipelines", description = "Manage pipeline definitions")
public class PipelineDefinitionController {

    private final PipelineDefinitionUseCase useCase;

    public PipelineDefinitionController(PipelineDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List pipelines")
    public Page<PipelineResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pipeline by id")
    public PipelineResult findById(@Parameter(description = "Pipeline id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a pipeline")
    public ResponseEntity<PipelineResult> create(@Valid @RequestBody CreatePipelineRequest request) {
        CreatePipelineCommand command = new CreatePipelineCommand(
                request.pipelineCode(),
                request.pipelineName(),
                request.description(),
                request.active());
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(command));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pipeline")
    public PipelineResult update(
            @Parameter(description = "Pipeline id") @PathVariable Long id,
            @Valid @RequestBody UpdatePipelineRequest request) {
        UpdatePipelineCommand command = new UpdatePipelineCommand(
                id,
                request.pipelineName(),
                request.description(),
                request.active());
        return useCase.update(command);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pipeline")
    public ResponseEntity<Void> delete(@Parameter(description = "Pipeline id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
