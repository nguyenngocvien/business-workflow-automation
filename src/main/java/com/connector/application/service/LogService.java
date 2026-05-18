package com.connector.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.exception.ResourceNotFoundException;
import com.connector.application.result.LogResult;
import com.connector.domain.entity.LogEntity;
import com.connector.domain.repository.LogRepository;
import com.connector.application.usecase.LogUseCase;

@Service
public class LogService implements LogUseCase {

    private final LogRepository repository;

    public LogService(LogRepository repository) {
        this.repository = repository;
    }

    public Page<LogResult> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public LogResult findById(Long logId) {
        return repository.findById(logId).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("EcLog not found with id: " + logId));
    }

    @Override
    public void delete(Long logId) {
        if (!repository.existsById(logId)) {
            throw new ResourceNotFoundException("EcLog not found with id: " + logId);
        }
        repository.deleteById(logId);
    }

    private LogResult toResponse(LogEntity entity) {
        return new LogResult(
            entity.getId(),
            entity.getService() != null ? entity.getService().getId() : null,
            entity.getTraceId(),
            entity.getCorrelationId(),
            entity.getRequestTime(),
            entity.getResponseTime(),
            entity.getDurationMs(),
            entity.getStatusCode(),
            entity.getRequestHeaders(),
            entity.getRequestBody(),
            entity.getRequestAfterTransform(),
            entity.getResponseBody(),
            entity.getResponseAfterTransform(),
            entity.getErrorMessage(),
            entity.getStacktrace(),
            entity.getCreatedAt()
        );
    }
}
