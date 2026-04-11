package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.dto.request.EcPipelineStepRequest;
import com.connector.application.dto.response.EcPipelineStepResponse;
import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcPipeline;
import com.connector.domain.entity.EcPipelineStep;
import com.connector.domain.entity.EcService;
import com.connector.domain.repository.EcPipelineRepository;
import com.connector.domain.repository.EcPipelineStepRepository;
import com.connector.domain.repository.EcServiceRepository;

@Service
public class EcPipelineStepApplicationService
    extends AbstractCrudApplicationService<EcPipelineStepRequest, EcPipelineStepResponse, EcPipelineStep, Long> {

    private final EcPipelineRepository pipelineRepository;
    private final EcServiceRepository serviceRepository;

    public EcPipelineStepApplicationService(
        EcPipelineStepRepository repository,
        EcPipelineRepository pipelineRepository,
        EcServiceRepository serviceRepository
    ) {
        super(repository, "EcPipelineStep");
        this.pipelineRepository = pipelineRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    protected EcPipelineStep newEntity() {
        return new EcPipelineStep();
    }

    @Override
    protected EcPipelineStepResponse toResponse(EcPipelineStep entity) {
        return new EcPipelineStepResponse(
            entity.getId(),
            entity.getPipeline() != null ? entity.getPipeline().getId() : null,
            entity.getStepOrder(),
            entity.getService() != null ? entity.getService().getId() : null,
            entity.getStepName(),
            entity.getRequestTransform(),
            entity.getResponseTransform(),
            entity.getContinueOnError(),
            entity.getCreatedAt()
        );
    }

    @Override
    protected void updateEntity(EcPipelineStep entity, EcPipelineStepRequest request, boolean creating) {
        entity.setPipeline(resolvePipeline(request.pipelineId()));
        entity.setStepOrder(request.stepOrder());
        entity.setService(resolveService(request.serviceId()));
        entity.setStepName(request.stepName());
        entity.setRequestTransform(request.requestTransform());
        entity.setResponseTransform(request.responseTransform());
        entity.setContinueOnError(
            request.continueOnError() != null ? request.continueOnError() : Boolean.FALSE
        );
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
    }

    private EcPipeline resolvePipeline(Long pipelineId) {
        if (pipelineId == null) {
            return null;
        }
        return pipelineRepository.findById(pipelineId)
            .orElseThrow(() -> new ResourceNotFoundException("EcPipeline not found with id: " + pipelineId));
    }

    private EcService resolveService(Long serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("EcService not found with id: " + serviceId));
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
