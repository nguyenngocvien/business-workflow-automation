package com.connector.application.service.executor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.connector.domain.enums.ServiceType;

@Component
public class ServiceExecutorRegistry {

    private final Map<ServiceType, ServiceExecutor> executors;
    
    public ServiceExecutorRegistry(List<ServiceExecutor> list) {
        this.executors = list.stream()
            .collect(Collectors.toMap(ServiceExecutor::supportedType, e -> e));
    }

    public ServiceExecutor get(ServiceType type) {
        return executors.get(type);
    }
}