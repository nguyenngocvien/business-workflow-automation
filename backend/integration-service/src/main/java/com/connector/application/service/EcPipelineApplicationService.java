package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.dto.request.EcPipelineRequest;
import com.connector.application.dto.response.EcPipelineResponse;
import com.connector.domain.entity.EcPipeline;
import com.connector.domain.repository.EcPipelineRepository;

@Service
public class EcPipelineApplicationService
    extends AbstractCrudApplicationService<EcPipelineRequest, EcPipelineResponse, EcPipeline, Long> {

    public EcPipelineApplicationService(EcPipelineRepository repository) {
        super(repository, "EcPipeline");
    }

    @Override
    protected EcPipeline newEntity() {
        return new EcPipeline();
    }

    @Override
    protected EcPipelineResponse toResponse(EcPipeline entity) {
        return new EcPipelineResponse(
            entity.getId(),
            entity.getPipelineCode(),
            entity.getPipelineName(),
            entity.getDescription(),
            entity.getActive(),
            entity.getCreatedAt()
        );
    }

    @Override
    protected void updateEntity(EcPipeline entity, EcPipelineRequest request, boolean creating) {
        entity.setPipelineCode(request.pipelineCode());
        entity.setPipelineName(request.pipelineName());
        entity.setDescription(request.description());
        entity.setActive(request.active() != null ? request.active() : Boolean.TRUE);
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
