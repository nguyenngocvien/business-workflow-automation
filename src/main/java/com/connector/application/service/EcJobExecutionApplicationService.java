package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.dto.request.EcJobExecutionRequest;
import com.connector.application.dto.response.EcJobExecutionResponse;
import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcJobExecution;
import com.connector.domain.entity.EcScheduleJob;
import com.connector.domain.repository.EcJobExecutionRepository;
import com.connector.domain.repository.EcScheduleJobRepository;

@Service
public class EcJobExecutionApplicationService
    extends AbstractCrudApplicationService<EcJobExecutionRequest, EcJobExecutionResponse, EcJobExecution, Long> {

    private final EcScheduleJobRepository scheduleJobRepository;

    public EcJobExecutionApplicationService(
        EcJobExecutionRepository repository,
        EcScheduleJobRepository scheduleJobRepository
    ) {
        super(repository, "EcJobExecution");
        this.scheduleJobRepository = scheduleJobRepository;
    }

    @Override
    protected EcJobExecution newEntity() {
        return new EcJobExecution();
    }

    @Override
    protected EcJobExecutionResponse toResponse(EcJobExecution entity) {
        return new EcJobExecutionResponse(
            entity.getId(),
            entity.getJob() != null ? entity.getJob().getId() : null,
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getStatus(),
            entity.getRequestData(),
            entity.getResponseData(),
            entity.getErrorMessage(),
            entity.getCreatedAt()
        );
    }

    @Override
    protected void updateEntity(EcJobExecution entity, EcJobExecutionRequest request, boolean creating) {
        entity.setJob(resolveJob(request.jobId()));
        entity.setStartTime(request.startTime());
        entity.setEndTime(request.endTime());
        entity.setStatus(request.status());
        entity.setRequestData(request.requestData());
        entity.setResponseData(request.responseData());
        entity.setErrorMessage(request.errorMessage());
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
    }

    private EcScheduleJob resolveJob(Long jobId) {
        if (jobId == null) {
            return null;
        }
        return scheduleJobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("EcScheduleJob not found with id: " + jobId));
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
