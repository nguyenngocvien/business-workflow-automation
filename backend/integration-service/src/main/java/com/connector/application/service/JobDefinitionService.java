package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreateJobExecutionCommand;
import com.connector.application.command.UpdateJobExecutionCommand;
import com.connector.application.exception.ResourceNotFoundException;
import com.connector.application.result.JobExecutionResult;
import com.connector.domain.entity.JobExecutionEntity;
import com.connector.domain.entity.ScheduleJobEntity;
import com.connector.domain.repository.JobExecutionRepository;
import com.connector.domain.repository.ScheduleJobRepository;
import com.connector.application.usecase.JobExecutionDefinitionUseCase;

@Service
public class JobDefinitionService implements JobExecutionDefinitionUseCase {

    private final JobExecutionRepository repository;
    private final ScheduleJobRepository scheduleJobRepository;

    public JobDefinitionService(
        JobExecutionRepository repository,
        ScheduleJobRepository scheduleJobRepository
    ) {
        this.repository = repository;
        this.scheduleJobRepository = scheduleJobRepository;
    }

    public Page<JobExecutionResult> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public JobExecutionResult findById(Long id) {
        return repository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("EcJobExecution not found with id: " + id));
    }
    
    @Override
    public JobExecutionResult create(CreateJobExecutionCommand command) {
        JobExecutionEntity entity = new JobExecutionEntity();
        entity.setJob(resolveJob(command.jobId()));
        entity.setStartTime(command.startTime());
        entity.setRequestData(command.requestData());
        entity.setCreatedAt(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Override
    public JobExecutionResult update(UpdateJobExecutionCommand command) {
        JobExecutionEntity entity = repository.findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("EcJobExecution not found with id: " + command.id()));
            
        entity.setEndTime(command.endTime());
        entity.setStatus(command.status());
        entity.setResponseData(command.responseData());
        entity.setErrorMessage(command.errorMessage());

        return toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) { 
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("EcJobExecution not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private JobExecutionResult toResponse(JobExecutionEntity entity) {
        return new JobExecutionResult(
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

    private ScheduleJobEntity resolveJob(Long jobId) {
        if (jobId == null) {
            return null;
        }
        return scheduleJobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("EcScheduleJob not found with id: " + jobId));
    }
}
