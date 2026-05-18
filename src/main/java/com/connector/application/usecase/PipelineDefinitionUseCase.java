package com.connector.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreatePipelineCommand;
import com.connector.application.command.UpdatePipelineCommand;
import com.connector.application.result.PipelineResult;

public interface PipelineDefinitionUseCase {

    Page<PipelineResult> findAll(Pageable pageable);

    PipelineResult findById(Long id);

    PipelineResult create(CreatePipelineCommand command);

    PipelineResult update(UpdatePipelineCommand command);

    void delete(Long id);
}
