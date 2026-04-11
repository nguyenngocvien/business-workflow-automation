package com.connector.application.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.application.service.execution.ExecutionLogService;
import com.connector.application.service.execution.ServiceExecutionException;
import com.connector.application.service.execution.TypedServiceExecutor;
import com.connector.application.service.mapper.NoOpServiceExecutionMapper;
import com.connector.application.service.mapper.ServiceExecutionMapper;
import com.connector.common.exception.ResourceNotFoundException;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ServiceType;
import com.connector.domain.repository.EcServiceRepository;

@Service
public class ServiceExecutionApplicationService {

    private final EcServiceRepository serviceRepository;
    private final Map<ServiceType, TypedServiceExecutor> executors;
    private final ExecutionLogService executionLogService;
    private final List<ServiceExecutionMapper> executionMappers;
    private final NoOpServiceExecutionMapper noOpServiceExecutionMapper;

    public ServiceExecutionApplicationService(
        EcServiceRepository serviceRepository,
        ExecutionLogService executionLogService,
        List<ServiceExecutionMapper> executionMappers,
        NoOpServiceExecutionMapper noOpServiceExecutionMapper,
        java.util.List<TypedServiceExecutor> executors
    ) {
        this.serviceRepository = serviceRepository;
        this.executionLogService = executionLogService;
        this.executionMappers = executionMappers;
        this.noOpServiceExecutionMapper = noOpServiceExecutionMapper;
        this.executors = executors.stream()
            .collect(Collectors.toMap(TypedServiceExecutor::supportedType, Function.identity()));
    }

    public ExecuteServiceResponse execute(
        String appId,
        String serviceCode,
        String serviceVersion,
        ExecuteServiceRequest request
    ) {
        EcService service = serviceRepository.findByAppIdAndServiceCodeAndServiceVersion(
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

        TypedServiceExecutor executor = executors.get(service.getServiceType());
        if (executor == null) {
            throw new IllegalArgumentException("Unsupported service execution type: " + service.getServiceType());
        }

        LocalDateTime requestTime = LocalDateTime.now();
        ExecuteServiceRequest enrichedRequest = enrichRequestHeaders(request);
        ServiceExecutionMapper mapper = resolveMapper(service);
        ExecuteServiceRequest transformedRequest = mapper.mapRequest(service, enrichedRequest);
        String traceId = enrichedRequest.headers() != null ? enrichedRequest.headers().get("X-Trace-Id") : null;
        String correlationId = enrichedRequest.headers() != null ? enrichedRequest.headers().get("X-Correlation-Id") : null;

        try {
            ExecuteServiceResponse rawResponse = executor.execute(service, transformedRequest);
            ExecuteServiceResponse transformedResponse = mapper.mapResponse(service, rawResponse);
            executionLogService.saveSuccess(
                service,
                traceId,
                correlationId,
                requestTime,
                org.springframework.http.HttpStatusCode.valueOf(rawResponse.statusCode()),
                toHttpHeaders(enrichedRequest.headers()),
                enrichedRequest.payload(),
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
                toHttpHeaders(enrichedRequest.headers()),
                enrichedRequest.payload(),
                transformedRequest.payload(),
                ex.getResponseBody(),
                null,
                ex
            );
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    private ExecuteServiceRequest enrichRequestHeaders(ExecuteServiceRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (request != null && request.headers() != null) {
            headers.putAll(request.headers());
        }
        headers.putIfAbsent("X-Trace-Id", UUID.randomUUID().toString());
        headers.putIfAbsent("X-Correlation-Id", UUID.randomUUID().toString());

        return new ExecuteServiceRequest(
            headers,
            request != null ? request.payload() : null
        );
    }

    private ServiceExecutionMapper resolveMapper(EcService service) {
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
