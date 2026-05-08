package com.connector.application.service.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.connector.domain.enums.ServiceType;

@Component
public class ServiceDefinitionRegistry {

    private final Map<ServiceType, ServiceDefinitionHandler> handlers;

    public ServiceDefinitionRegistry(List<ServiceDefinitionHandler> handlerList) {

        if (handlerList == null || handlerList.isEmpty()) {
            throw new IllegalStateException("No ServiceDefinitionHandler found");
        }

        this.handlers = handlerList.stream()
            .collect(Collectors.toMap(
                ServiceDefinitionHandler::type,
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

    public ServiceDefinitionHandler get(ServiceType type) {

        if (type == null) {
            throw new IllegalArgumentException("ServiceType must not be null");
        }

        ServiceDefinitionHandler handler = handlers.get(type);

        if (handler == null) {
            throw new IllegalArgumentException(
                "No handler found for ServiceType: " + type
                    + ". Registered: " + handlers.keySet()
            );
        }

        return handler;
    }

    public Map<ServiceType, ServiceDefinitionHandler> getAll() {
        return handlers;
    }
}