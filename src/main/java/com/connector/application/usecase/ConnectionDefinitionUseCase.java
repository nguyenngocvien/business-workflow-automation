package com.connector.application.usecase;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreateConnectionCommand;
import com.connector.application.command.UpdateConnectionCommand;
import com.connector.application.result.ConnectionResult;
import com.connector.domain.enums.ConnectionType;

public interface ConnectionDefinitionUseCase {

    Page<ConnectionResult> findAll(Pageable pageable);

    ConnectionResult findById(Long id);

    ConnectionResult create(CreateConnectionCommand command);

    ConnectionResult update(UpdateConnectionCommand command);

    void delete(Long id);

    Map<String, Object> schema(ConnectionType connectionType);
}
