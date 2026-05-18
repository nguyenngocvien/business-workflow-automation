package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreatePipelineStepCommand;
import com.connector.application.command.UpdatePipelineStepCommand;
import com.connector.application.exception.ResourceNotFoundException;
import com.connector.application.result.PipelineStepResult;
import com.connector.domain.entity.PipelineEntity;
import com.connector.domain.entity.PipelineStepEntity;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.repository.PipelineRepository;
import com.connector.domain.repository.PipelineStepRepository;
import com.connector.domain.repository.ServiceRepository;
import com.connector.application.usecase.PipelineStepDefinitionUseCase;

@Service
public class PipelineStepDefinitionService implements PipelineStepDefinitionUseCase {

    private final PipelineStepRepository pipelineStepRepository;
    private final PipelineRepository pipelineRepository;
    private final ServiceRepository serviceRepository;

    public PipelineStepDefinitionService(
            PipelineStepRepository pipelineStepRepository,
            PipelineRepository pipelineRepository,
            ServiceRepository serviceRepository) {
        this.pipelineStepRepository = pipelineStepRepository;
        this.pipelineRepository = pipelineRepository;
        this.serviceRepository = serviceRepository;
    }

    public Page<PipelineStepResult> findAll(Pageable pageable) {
        return pipelineStepRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public PipelineStepResult findById(Long id) {
        return pipelineStepRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("EcPipelineStep not found with id: " + id));
    }

    @Override
    public PipelineStepResult create(CreatePipelineStepCommand command) {
        PipelineStepEntity entity = new PipelineStepEntity();
        entity.setPipeline(resolvePipeline(command.pipelineId()));
        entity.setStepOrder(command.stepOrder());
        entity.setService(resolveService(command.serviceId()));
        entity.setStepName(command.stepName());
        entity.setRequestTransform(command.requestTransform());
        entity.setResponseTransform(command.responseTransform());
        entity.setContinueOnError(
                command.continueOnError() != null ? command.continueOnError() : Boolean.FALSE);
        entity.setCreatedAt(LocalDateTime.now());

        return toResponse(pipelineStepRepository.save(entity));
    }

    @Override
    public PipelineStepResult update(UpdatePipelineStepCommand command) {
        PipelineStepEntity entity = pipelineStepRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("EcPipelineStep not found with id: " + command.id()));

        entity.setStepOrder(command.stepOrder());
        entity.setService(resolveService(command.serviceId()));
        entity.setStepName(command.stepName());
        entity.setRequestTransform(command.requestTransform());
        entity.setResponseTransform(command.responseTransform());
        entity.setContinueOnError(
                command.continueOnError() != null ? command.continueOnError() : Boolean.FALSE);

        return toResponse(pipelineStepRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!pipelineStepRepository.existsById(id)) {
            throw new ResourceNotFoundException("EcPipelineStep not found with id: " + id);
        }
        pipelineStepRepository.deleteById(id);
    }

    private PipelineStepResult toResponse(PipelineStepEntity entity) {
        return new PipelineStepResult(
                entity.getId(),
                entity.getPipeline() != null ? entity.getPipeline().getId() : null,
                entity.getStepOrder(),
                entity.getService() != null ? entity.getService().getId() : null,
                entity.getStepName(),
                entity.getRequestTransform(),
                entity.getResponseTransform(),
                entity.getContinueOnError(),
                entity.getCreatedAt());
    }

    private PipelineEntity resolvePipeline(Long pipelineId) {
        if (pipelineId == null) {
            return null;
        }
        return pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("EcPipeline not found with id: " + pipelineId));
    }

    private ServiceEntity resolveService(Long serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("EcService not found with id: " + serviceId));
    }
}
