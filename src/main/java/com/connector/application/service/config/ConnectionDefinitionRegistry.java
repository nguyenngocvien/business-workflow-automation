package com.connector.application.service.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.connector.domain.enums.ConnectionType;

@Component
public class ConnectionDefinitionRegistry {

    private final Map<ConnectionType, ConnectionDefinitionHandler> handlers;

    public ConnectionDefinitionRegistry(List<ConnectionDefinitionHandler> handlerList) {

        if (handlerList == null || handlerList.isEmpty()) {
            throw new IllegalStateException("No ConnectionDefinitionHandler found");
        }

        this.handlers = handlerList.stream()
            .collect(Collectors.toMap(
                ConnectionDefinitionHandler::type,
                Function.identity(),
                (a, b) -> {
                    throw new IllegalStateException(
                        "Duplicate handler for type: " + a.type()
                            + " -> " + a.getClass().getSimpleName()
                            + " & " + b.getClass().getSimpleName()
                    );
                }
            ));
    }

    public ConnectionDefinitionHandler get(ConnectionType type) {

        if (type == null) {
            throw new IllegalArgumentException("ConnectionType must not be null");
        }

        ConnectionDefinitionHandler handler = handlers.get(type);

        if (handler == null) {
            throw new IllegalArgumentException(
                "No handler found for ConnectionType: " + type
                    + ". Registered types: " + handlers.keySet()
            );
        }

        return handler;
    }

    public Map<ConnectionType, ConnectionDefinitionHandler> getAll() {
        return handlers;
    }
}