package com.connector.application.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreateConnectionCommand;
import com.connector.application.command.UpdateConnectionCommand;
import com.connector.domain.entity.ConnectionEntity;
import com.connector.domain.enums.ConnectionType;
import com.connector.domain.repository.ConnectionRepository;
import com.connector.application.result.ConnectionResult;
import com.connector.application.port.out.EncryptionEngine;
import com.connector.application.service.config.ConnectionDefinitionHandler;
import com.connector.application.service.config.ConnectionDefinitionRegistry;
import com.connector.application.usecase.ConnectionDefinitionUseCase;

@Service
public class ConnectionDefinitionService implements ConnectionDefinitionUseCase {

    private final ConnectionRepository connectionRepository;
    private final ConnectionDefinitionRegistry definitions;
    private final EncryptionEngine encryptionEngine;

    public ConnectionDefinitionService(
            ConnectionRepository connectionRepository,
            ConnectionDefinitionRegistry definitions,
            EncryptionEngine encryptionEngine) {
        this.connectionRepository = connectionRepository;
        this.definitions = definitions;
        this.encryptionEngine = encryptionEngine;
    }

    public Page<ConnectionResult> findAll(Pageable pageable) {
        return connectionRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ConnectionResult findById(Long id) {
        return connectionRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));
    }

    @Override
    public ConnectionResult create(CreateConnectionCommand command) {

        if (command.config() == null) {
            throw new IllegalArgumentException("config is required");
        }

        ConnectionDefinitionHandler handler = definitions.get(command.connectionType());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + command.connectionType());
        }

        String configJson = handler.serialize(command.config());
        String encryptedConfigJson = encryptionEngine.encrypt(configJson);

        ConnectionEntity entity = new ConnectionEntity();
        entity.setConnectionCode(command.connectionCode());
        entity.setConnectionName(command.connectionName());
        entity.setConnectionType(command.connectionType());
        entity.setConfigJson(encryptedConfigJson);
        entity.setActive(command.active() != null ? command.active() : Boolean.TRUE);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return toResponse(connectionRepository.save(entity));
    }

    @Override
    public ConnectionResult update(UpdateConnectionCommand command) {
        Long id = command.id();

        if (command.config() == null) {
            throw new IllegalArgumentException("config is required");
        }

        ConnectionDefinitionHandler handler = definitions.get(command.connectionType());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + command.connectionType());
        }

        String configJson = handler.serialize(command.config());
        String encryptedConfigJson = encryptionEngine.encrypt(configJson);

        ConnectionEntity entity = connectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));
        entity.setConnectionName(command.connectionName());
        entity.setConnectionType(command.connectionType());
        entity.setConfigJson(encryptedConfigJson);
        entity.setActive(command.active() != null ? command.active() : Boolean.TRUE);
        entity.setUpdatedAt(LocalDateTime.now());
        return toResponse(connectionRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        connectionRepository.deleteById(id);
    }

    protected ConnectionResult toResponse(ConnectionEntity entity) {
        ConnectionDefinitionHandler handler = definitions.get(entity.getConnectionType());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + entity.getConnectionType());
        }

        String decryptedConfigJson = entity.getConfigJson() != null
            ? encryptionEngine.decrypt(entity.getConfigJson())
            : null;

        return new ConnectionResult(
                entity.getId(),
                entity.getConnectionCode(),
                entity.getConnectionName(),
                entity.getConnectionType(),
                decryptedConfigJson,
                decryptedConfigJson != null ? handler.deserialize(decryptedConfigJson) : null,
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public Map<String, Object> schema(ConnectionType connectionType) {
        ConnectionDefinitionHandler handler = definitions.get(connectionType);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type: " + connectionType);
        }
        return handler.schema();
    }
}
