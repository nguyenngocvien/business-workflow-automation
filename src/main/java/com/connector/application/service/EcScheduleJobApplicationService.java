package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.dto.request.EcScheduleJobRequest;
import com.connector.application.dto.response.EcScheduleJobResponse;
import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcPipeline;
import com.connector.domain.entity.EcScheduleJob;
import com.connector.domain.entity.EcService;
import com.connector.domain.repository.EcPipelineRepository;
import com.connector.domain.repository.EcScheduleJobRepository;
import com.connector.domain.repository.EcServiceRepository;

@Service
public class EcScheduleJobApplicationService
    extends AbstractCrudApplicationService<EcScheduleJobRequest, EcScheduleJobResponse, EcScheduleJob, Long> {

    private final EcServiceRepository serviceRepository;
    private final EcPipelineRepository pipelineRepository;

    public EcScheduleJobApplicationService(
        EcScheduleJobRepository repository,
        EcServiceRepository serviceRepository,
        EcPipelineRepository pipelineRepository
    ) {
        super(repository, "EcScheduleJob");
        this.serviceRepository = serviceRepository;
        this.pipelineRepository = pipelineRepository;
    }

    @Override
    protected EcScheduleJob newEntity() {
        return new EcScheduleJob();
    }

    @Override
    protected EcScheduleJobResponse toResponse(EcScheduleJob entity) {
        return new EcScheduleJobResponse(
            entity.getId(),
            entity.getJobCode(),
            entity.getJobName(),
            entity.getJobType(),
            entity.getService() != null ? entity.getService().getId() : null,
            entity.getPipeline() != null ? entity.getPipeline().getId() : null,
            entity.getCronExpression(),
            entity.getFixedRateMs(),
            entity.getEnabled(),
            entity.getLastRunTime(),
            entity.getNextRunTime(),
            entity.getCreatedAt()
        );
    }

    @Override
    protected void updateEntity(EcScheduleJob entity, EcScheduleJobRequest request, boolean creating) {
        entity.setJobCode(request.jobCode());
        entity.setJobName(request.jobName());
        entity.setJobType(request.jobType());
        entity.setService(resolveService(request.serviceId()));
        entity.setPipeline(resolvePipeline(request.pipelineId()));
        entity.setCronExpression(request.cronExpression());
        entity.setFixedRateMs(request.fixedRateMs());
        entity.setEnabled(request.enabled() != null ? request.enabled() : Boolean.TRUE);
        entity.setLastRunTime(request.lastRunTime());
        entity.setNextRunTime(request.nextRunTime());
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
    }

    private EcService resolveService(Long serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("EcService not found with id: " + serviceId));
    }

    private EcPipeline resolvePipeline(Long pipelineId) {
        if (pipelineId == null) {
            return null;
        }
        return pipelineRepository.findById(pipelineId)
            .orElseThrow(() -> new ResourceNotFoundException("EcPipeline not found with id: " + pipelineId));
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
