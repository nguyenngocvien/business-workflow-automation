package com.connector.application.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreateServiceCommand;
import com.connector.application.command.UpdateServiceCommand;
import com.connector.application.port.out.EncryptionEngine;
import com.connector.domain.entity.ConnectionEntity;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;
import com.connector.domain.repository.ConnectionRepository;
import com.connector.domain.repository.ServiceRepository;
import com.connector.application.exception.ResourceNotFoundException;
import com.connector.application.result.ServiceResult;
import com.connector.application.service.config.ServiceDefinitionHandler;
import com.connector.application.service.config.ServiceDefinitionRegistry;
import com.connector.application.usecase.ServiceDefinitionUseCase;

@Service
public class ServiceDefinitionService implements ServiceDefinitionUseCase {

    private final ServiceRepository serviceRepository;
    private final ConnectionRepository connectionRepository;
    private final ServiceDefinitionRegistry definitions;
    private final EncryptionEngine encryptionEngine;

    public ServiceDefinitionService(
        ServiceRepository serviceRepository,
        ConnectionRepository connectionRepository,
        ServiceDefinitionRegistry definitions,
        EncryptionEngine encryptionEngine
    ) {
        this.serviceRepository = serviceRepository;
        this.connectionRepository = connectionRepository;
        this.definitions = definitions;
        this.encryptionEngine = encryptionEngine;
    }

    public Page<ServiceResult> findAll(Pageable pageable) {
        return serviceRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ServiceResult findById(Long id) {
        return serviceRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("EcService not found with id: " + id));
    }

    @Override
    public ServiceResult create(CreateServiceCommand command) {
        if (command.config() == null) {
            throw new IllegalArgumentException("Either config or configJson is required");
        }
        
        ServiceDefinitionHandler handler = definitions.get(command.serviceType());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + command.serviceType());
        }
        String configJson = handler.serialize(command.config());
        String encryptedConfigJson = encryptionEngine.encrypt(configJson);

        ServiceEntity entity = newEntity();
        entity.setServiceCode(command.serviceCode());
        entity.setServiceName(command.serviceName());
        entity.setServiceType(command.serviceType());
        entity.setServiceVersion(command.serviceVersion());
        entity.setAppId(command.appId());
        entity.setConnection(resolveConnection(command.connectionId()));
        entity.setConfigJson(encryptedConfigJson);
        entity.setTimeoutMs(command.timeoutMs());
        entity.setRetryCount(command.retryCount() != null ? command.retryCount() : 0);
        entity.setActive(command.active() != null ? command.active() : Boolean.TRUE);
        entity.setLogEnable(command.logEnable() != null ? command.logEnable() : Boolean.TRUE);
        entity.setCreatedBy(command.createdBy());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return toResponse(serviceRepository.save(entity));
    }

    @Override
    public ServiceResult update(UpdateServiceCommand command) {
        if (command.config() == null) {
            throw new IllegalArgumentException("Either config or configJson is required");
        }

        ServiceEntity entity = serviceRepository.findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("EcService not found with id: " + command.id()));
        
        ServiceDefinitionHandler handler = definitions.get(command.serviceType());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + command.serviceType());
        }
        String configJson = handler.serialize(command.config());
        String encryptedConfigJson = encryptionEngine.encrypt(configJson);

        entity.setServiceName(command.serviceName());
        entity.setServiceType(command.serviceType());
        entity.setServiceVersion(command.serviceVersion());
        entity.setAppId(command.appId());
        entity.setConnection(resolveConnection(command.connectionId()));
        entity.setConfigJson(encryptedConfigJson);
        entity.setTimeoutMs(command.timeoutMs());
        entity.setRetryCount(command.retryCount() != null ? command.retryCount() : 0);
        entity.setActive(command.active() != null ? command.active() : Boolean.TRUE);
        entity.setLogEnable(command.logEnable() != null ? command.logEnable() : Boolean.TRUE);
        entity.setUpdatedBy(command.updatedBy());
        entity.setUpdatedAt(LocalDateTime.now());
        return toResponse(serviceRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("EcService not found with id: " + id);
        }
        serviceRepository.deleteById(id);
    }

    private ServiceEntity newEntity() {
        return new ServiceEntity();
    }

    private ServiceResult toResponse(ServiceEntity entity) {
        ServiceDefinitionHandler handler = definitions.get(entity.getServiceType());
        String decryptedConfigJson = entity.getConfigJson() != null
            ? encryptionEngine.decrypt(entity.getConfigJson())
            : null;

        return new ServiceResult(
            entity.getId(),
            entity.getServiceCode(),
            entity.getServiceName(),
            entity.getServiceType(),
            entity.getServiceVersion(),
            entity.getAppId(),
            entity.getConnection() != null ? entity.getConnection().getId() : null,
            decryptedConfigJson,
            decryptedConfigJson != null && handler != null ? handler.deserialize(decryptedConfigJson) : null,
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

    private ConnectionEntity resolveConnection(Long connectionId) {
        if (connectionId == null) {
            return null;
        }
        return connectionRepository.findById(connectionId)
            .orElseThrow(() -> new ResourceNotFoundException("EcConnection not found with id: " + connectionId));
    }

    @Override
    public Map<String, Object> schema(ServiceType serviceType) {
        ServiceDefinitionHandler handler = definitions.get(serviceType);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + serviceType);
        }
        return handler.schema();
    }
}
