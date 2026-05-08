package com.connector.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreatePipelineStepCommand;
import com.connector.application.command.UpdatePipelineStepCommand;
import com.connector.application.result.PipelineStepResult;

public interface PipelineStepDefinitionUseCase {

    Page<PipelineStepResult> findAll(Pageable pageable);

    PipelineStepResult findById(Long id);

    PipelineStepResult create(CreatePipelineStepCommand command);

    PipelineStepResult update(UpdatePipelineStepCommand command);

    void delete(Long id);
}
