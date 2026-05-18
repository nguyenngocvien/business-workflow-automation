package com.connector.application.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.exception.ResourceNotFoundException;
import com.connector.application.exception.ServiceExecutionException;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.application.service.executor.ExecutionLogService;
import com.connector.application.service.executor.ServiceExecutor;
import com.connector.application.service.executor.ServiceExecutorRegistry;
import com.connector.application.service.mapper.NoOpServiceExecutionMapper;
import com.connector.application.service.mapper.ServiceExecutionMapper;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.repository.ServiceRepository;
import com.connector.application.usecase.ServiceExecutionUseCase;

@Service
public class ServiceExecutionService implements ServiceExecutionUseCase {

    private final ServiceRepository serviceRepository;
    private final ServiceExecutorRegistry executorRegistry;
    private final ExecutionLogService executionLogService;
    private final List<ServiceExecutionMapper> executionMappers;
    private final NoOpServiceExecutionMapper noOpServiceExecutionMapper;

    public ServiceExecutionService(
        ServiceRepository serviceRepository,
        ServiceExecutorRegistry executorRegistry,
        ExecutionLogService executionLogService,
        List<ServiceExecutionMapper> executionMappers,
        NoOpServiceExecutionMapper noOpServiceExecutionMapper
    ) {
        this.serviceRepository = serviceRepository;
        this.executorRegistry = executorRegistry;
        this.executionLogService = executionLogService;
        this.executionMappers = executionMappers;
        this.noOpServiceExecutionMapper = noOpServiceExecutionMapper;
    }

    @Override
    public ExecuteServiceResult execute(ExecuteServiceCommand command) {
        String appId = command.appId();
        String serviceCode = command.serviceCode();
        String serviceVersion = command.serviceVersion();
        ServiceEntity service = serviceRepository.findByAppIdAndServiceCodeAndServiceVersion(
            appId,
            serviceCode,
            serviceVersion
        ).orElseThrow(() -> new ResourceNotFoundException(
            "EcService not found with appId=%s, serviceCode=%s, serviceVersion=%s"
                .formatted(appId, serviceCode, serviceVersion)
        ));

        if (Boolean.FALSE.equals(service.getActive())) {
            throw new IllegalArgumentException("Service is inactive");
        }

        ServiceExecutor executor = executorRegistry.get(service.getServiceType());
        if (executor == null) {
            throw new IllegalArgumentException("Unsupported service execution type: " + service.getServiceType());
        }

        LocalDateTime requestTime = LocalDateTime.now();

        ServiceExecutionMapper mapper = resolveMapper(service);
        ExecuteServiceCommand transformedRequest = mapper.mapRequest(service, command);

        Map<String, String> enrichedHeaders = enrichRequestHeaders(transformedRequest.headers());
        String traceId = enrichedHeaders.get("X-Trace-Id");
        String correlationId = enrichedHeaders.get("X-Correlation-Id");

        try {
            ExecuteServiceResult rawResponse = executor.execute(service, transformedRequest);
            ExecuteServiceResult transformedResponse = mapper.mapResponse(service, rawResponse);
            executionLogService.saveSuccess(
                service,
                traceId,
                correlationId,
                requestTime,
                org.springframework.http.HttpStatusCode.valueOf(rawResponse.statusCode()),
                toHttpHeaders(enrichedHeaders),
                command.payload(),
                transformedRequest.payload(),
                rawResponse.body(),
                transformedResponse.body()
            );
            return transformedResponse;
        } catch (ServiceExecutionException ex) {
            executionLogService.saveFailure(
                service,
                traceId,
                correlationId,
                requestTime,
                ex.getStatusCode(),
                toHttpHeaders(enrichedHeaders),
                command.payload(),
                transformedRequest.payload(),
                ex.getResponseBody(),
                null,
                ex
            );
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    private Map<String, String> enrichRequestHeaders(Map<String, String> headers) {
        Map<String, String> enrichedHeaders = new LinkedHashMap<>();
        if (headers != null) {
            enrichedHeaders.putAll(headers);
        }
        enrichedHeaders.putIfAbsent("X-Trace-Id", UUID.randomUUID().toString());
        enrichedHeaders.putIfAbsent("X-Correlation-Id", UUID.randomUUID().toString());

        return enrichedHeaders;
    }

    private ServiceExecutionMapper resolveMapper(ServiceEntity service) {
        return executionMappers.stream()
            .filter(mapper -> mapper.supports(service))
            .findFirst()
            .orElse(noOpServiceExecutionMapper);
    }

    private HttpHeaders toHttpHeaders(Map<String, String> source) {
        HttpHeaders headers = new HttpHeaders();
        if (source != null) {
            source.forEach(headers::set);
        }
        return headers;
    }
}
