package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.dto.request.EcLogRequest;
import com.connector.application.dto.response.EcLogResponse;
import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcLog;
import com.connector.domain.entity.EcLogId;
import com.connector.domain.entity.EcService;
import com.connector.domain.repository.EcLogRepository;
import com.connector.domain.repository.EcServiceRepository;

@Service
public class EcLogApplicationService
    extends AbstractCrudApplicationService<EcLogRequest, EcLogResponse, EcLog, EcLogId> {

    private final EcServiceRepository serviceRepository;

    public EcLogApplicationService(EcLogRepository repository, EcServiceRepository serviceRepository) {
        super(repository, "EcLog");
        this.serviceRepository = serviceRepository;
    }

    @Override
    protected EcLog newEntity() {
        return new EcLog();
    }

    @Override
    protected EcLogResponse toResponse(EcLog entity) {
        return new EcLogResponse(
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

    @Override
    protected void updateEntity(EcLog entity, EcLogRequest request, boolean creating) {
        entity.setId(creating ? null : entity.getId());
        entity.setService(resolveService(request.serviceId()));
        entity.setTraceId(request.traceId());
        entity.setCorrelationId(request.correlationId());
        entity.setRequestTime(creating ? defaultNow(request.requestTime()) : entity.getRequestTime());
        entity.setResponseTime(request.responseTime());
        entity.setDurationMs(request.durationMs());
        entity.setStatusCode(request.statusCode());
        entity.setRequestHeaders(request.requestHeaders());
        entity.setRequestBody(request.requestBody());
        entity.setRequestAfterTransform(request.requestAfterTransform());
        entity.setResponseBody(request.responseBody());
        entity.setResponseAfterTransform(request.responseAfterTransform());
        entity.setErrorMessage(request.errorMessage());
        entity.setStacktrace(request.stacktrace());
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
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
