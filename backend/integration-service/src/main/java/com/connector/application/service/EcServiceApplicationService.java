package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.service.config.ServiceConfigSupportService;
import com.connector.application.dto.request.EcServiceRequest;
import com.connector.application.dto.response.EcServiceResponse;
import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcConnection;
import com.connector.domain.entity.EcService;
import com.connector.domain.repository.EcConnectionRepository;
import com.connector.domain.repository.EcServiceRepository;

@Service
public class EcServiceApplicationService
    extends AbstractCrudApplicationService<EcServiceRequest, EcServiceResponse, EcService, Long> {

    private final EcConnectionRepository connectionRepository;
    private final ServiceConfigSupportService serviceConfigSupportService;

    public EcServiceApplicationService(
        EcServiceRepository repository,
        EcConnectionRepository connectionRepository,
        ServiceConfigSupportService serviceConfigSupportService
    ) {
        super(repository, "EcService");
        this.connectionRepository = connectionRepository;
        this.serviceConfigSupportService = serviceConfigSupportService;
    }

    @Override
    protected EcService newEntity() {
        return new EcService();
    }

    @Override
    protected EcServiceResponse toResponse(EcService entity) {
        return new EcServiceResponse(
            entity.getId(),
            entity.getServiceCode(),
            entity.getServiceName(),
            entity.getServiceType(),
            entity.getServiceVersion(),
            entity.getAppId(),
            entity.getConnection() != null ? entity.getConnection().getId() : null,
            entity.getConfigJson(),
            serviceConfigSupportService.deserialize(entity.getServiceType(), entity.getConfigJson()),
            entity.getTimeoutMs(),
            entity.getRetryCount(),
            entity.getActive(),
            entity.getLogEnable(),
            entity.getCreatedBy(),
            entity.getUpdatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    @Override
    protected void updateEntity(EcService entity, EcServiceRequest request, boolean creating) {
        if (request.config() == null && (request.configJson() == null || request.configJson().isBlank())) {
            throw new IllegalArgumentException("Either config or configJson is required");
        }
        entity.setServiceCode(request.serviceCode());
        entity.setServiceName(request.serviceName());
        entity.setServiceType(request.serviceType());
        entity.setServiceVersion(request.serviceVersion());
        entity.setAppId(request.appId());
        entity.setConnection(resolveConnection(request.connectionId()));
        entity.setConfigJson(
            serviceConfigSupportService.serialize(
                request.serviceType(),
                request.config(),
                request.configJson()
            )
        );
        entity.setTimeoutMs(request.timeoutMs());
        entity.setRetryCount(request.retryCount() != null ? request.retryCount() : 0);
        entity.setActive(request.active() != null ? request.active() : Boolean.TRUE);
        entity.setLogEnable(request.logEnable() != null ? request.logEnable() : Boolean.TRUE);
        entity.setCreatedBy(request.createdBy());
        entity.setUpdatedBy(request.updatedBy());
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
        entity.setUpdatedAt(defaultNow(request.updatedAt()));
    }

    private EcConnection resolveConnection(Long connectionId) {
        if (connectionId == null) {
            return null;
        }
        return connectionRepository.findById(connectionId)
            .orElseThrow(() -> new ResourceNotFoundException("EcConnection not found with id: " + connectionId));
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
