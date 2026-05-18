package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreateScheduleJobCommand;
import com.connector.application.command.UpdateScheduleJobCommand;
import com.connector.application.exception.ResourceNotFoundException;
import com.connector.application.result.ScheduleJobResult;
import com.connector.domain.entity.PipelineEntity;
import com.connector.domain.entity.ScheduleJobEntity;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.repository.PipelineRepository;
import com.connector.domain.repository.ScheduleJobRepository;
import com.connector.domain.repository.ServiceRepository;
import com.connector.application.usecase.ScheduleJobDefinitionUseCase;

@Service
public class ScheduleJobDefinitionService implements ScheduleJobDefinitionUseCase {

    private final ScheduleJobRepository scheduleJobRepository;
    private final ServiceRepository serviceRepository;
    private final PipelineRepository pipelineRepository;

    public ScheduleJobDefinitionService(
        ScheduleJobRepository scheduleJobRepository,
        ServiceRepository serviceRepository,
        PipelineRepository pipelineRepository
    ) {
        this.scheduleJobRepository = scheduleJobRepository;
        this.serviceRepository = serviceRepository;
        this.pipelineRepository = pipelineRepository;
    }

    public Page<ScheduleJobResult> findAll(Pageable pageable) {
        return scheduleJobRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ScheduleJobResult findById(Long id) {
        return scheduleJobRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("EcScheduleJob not found with id: " + id));
    }

    @Override
    public ScheduleJobResult create(CreateScheduleJobCommand command) {
        ScheduleJobEntity entity = newEntity();entity.setJobCode(command.jobCode());
        entity.setJobName(command.jobName());
        entity.setJobType(command.jobType());
        entity.setService(resolveService(command.serviceId()));
        entity.setPipeline(resolvePipeline(command.pipelineId()));
        entity.setCronExpression(command.cronExpression());
        entity.setFixedRateMs(command.fixedRateMs());
        entity.setEnabled(command.enabled() != null ? command.enabled() : Boolean.TRUE);
        entity.setCreatedAt(LocalDateTime.now());
        return toResponse(scheduleJobRepository.save(entity));
    }

    @Override
    public ScheduleJobResult update(UpdateScheduleJobCommand command) {
        ScheduleJobEntity entity = scheduleJobRepository.findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("ScheduleJob not found with id: " + command.id()));

        entity.setJobName(command.jobName());
        entity.setJobType(command.jobType());
        entity.setService(resolveService(command.serviceId()));
        entity.setPipeline(resolvePipeline(command.pipelineId()));
        entity.setCronExpression(command.cronExpression());
        entity.setFixedRateMs(command.fixedRateMs());
        entity.setEnabled(command.enabled() != null ? command.enabled() : Boolean.TRUE);
        return toResponse(scheduleJobRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!scheduleJobRepository.existsById(id)) {
            throw new ResourceNotFoundException("EcScheduleJob not found with id: " + id);
        }
        scheduleJobRepository.deleteById(id);
    }

    private ScheduleJobEntity newEntity() {
        return new ScheduleJobEntity();
    }

    private ScheduleJobResult toResponse(ScheduleJobEntity entity) {
        return new ScheduleJobResult(
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

    private ServiceEntity resolveService(Long serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("EcService not found with id: " + serviceId));
    }

    private PipelineEntity resolvePipeline(Long pipelineId) {
        if (pipelineId == null) {
            return null;
        }
        return pipelineRepository.findById(pipelineId)
            .orElseThrow(() -> new ResourceNotFoundException("EcPipeline not found with id: " + pipelineId));
    }
}
