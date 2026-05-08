package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreatePipelineCommand;
import com.connector.application.command.UpdatePipelineCommand;
import com.connector.application.result.PipelineResult;
import com.connector.domain.entity.PipelineEntity;
import com.connector.domain.repository.PipelineRepository;
import com.connector.application.usecase.PipelineDefinitionUseCase;

@Service
public class PipelineDefinitionService implements PipelineDefinitionUseCase {

    private final PipelineRepository repository;

    public PipelineDefinitionService(PipelineRepository repository) {
        this.repository = repository;
    }

    public Page<PipelineResult> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResult);
    }

    @Override
    public PipelineResult findById(Long id) {
        return repository.findById(id).map(this::toResult)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));
    }
    
    @Override
    public PipelineResult create(CreatePipelineCommand command) {
        PipelineEntity entity = new PipelineEntity();
        entity.setPipelineCode(command.pipelineCode());
        entity.setPipelineName(command.pipelineName());
        entity.setDescription(command.description());
        entity.setActive(command.active() != null ? command.active() : Boolean.TRUE);
        entity.setCreatedAt(LocalDateTime.now());
        return toResult(repository.save(entity));
    }

    @Override
    public PipelineResult update(UpdatePipelineCommand command) {
        PipelineEntity entity = repository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        entity.setPipelineName(command.pipelineName());
        entity.setDescription(command.description());
        entity.setActive(command.active() != null ? command.active() : entity.getActive());

        return toResult(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private PipelineResult toResult(PipelineEntity entity) {
        return new PipelineResult(
            entity.getId(),
            entity.getPipelineCode(),
            entity.getPipelineName(),
            entity.getDescription(),
            entity.getActive(),
            entity.getCreatedAt()
        );
    }
}
