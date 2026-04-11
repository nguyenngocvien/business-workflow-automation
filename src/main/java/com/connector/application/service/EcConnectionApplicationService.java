package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.service.config.ConnectionConfigSupportService;
import com.connector.application.dto.request.EcConnectionRequest;
import com.connector.application.dto.response.EcConnectionResponse;
import com.connector.domain.entity.EcConnection;
import com.connector.domain.repository.EcConnectionRepository;

@Service
public class EcConnectionApplicationService
    extends AbstractCrudApplicationService<EcConnectionRequest, EcConnectionResponse, EcConnection, Long> {

    private final ConnectionConfigSupportService connectionConfigSupportService;

    public EcConnectionApplicationService(
        EcConnectionRepository repository,
        ConnectionConfigSupportService connectionConfigSupportService
    ) {
        super(repository, "EcConnection");
        this.connectionConfigSupportService = connectionConfigSupportService;
    }

    @Override
    protected EcConnection newEntity() {
        return new EcConnection();
    }

    @Override
    protected EcConnectionResponse toResponse(EcConnection entity) {
        return new EcConnectionResponse(
            entity.getId(),
            entity.getConnectionCode(),
            entity.getConnectionName(),
            entity.getConnectionType(),
            entity.getConfigJson(),
            connectionConfigSupportService.deserialize(entity.getConnectionType(), entity.getConfigJson()),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    @Override
    protected void updateEntity(EcConnection entity, EcConnectionRequest request, boolean creating) {
        if (request.config() == null && (request.configJson() == null || request.configJson().isBlank())) {
            throw new IllegalArgumentException("Either config or configJson is required");
        }
        entity.setConnectionCode(request.connectionCode());
        entity.setConnectionName(request.connectionName());
        entity.setConnectionType(request.connectionType());
        entity.setConfigJson(
            connectionConfigSupportService.serialize(
                request.connectionType(),
                request.config(),
                request.configJson()
            )
        );
        entity.setActive(request.active() != null ? request.active() : Boolean.TRUE);
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
        entity.setUpdatedAt(defaultNow(request.updatedAt()));
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
