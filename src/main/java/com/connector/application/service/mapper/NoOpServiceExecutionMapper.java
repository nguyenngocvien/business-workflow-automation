package com.connector.application.service.mapper;

import org.springframework.stereotype.Component;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ServiceType;

@Component
public class NoOpServiceExecutionMapper implements ServiceExecutionMapper {

    @Override
    public ServiceType supportedType() {
        return null;
    }

    @Override
    public String supportedServiceCode() {
        return "";
    }

    @Override
    public ExecuteServiceRequest mapRequest(EcService service, ExecuteServiceRequest request) {
        return request;
    }

    @Override
    public ExecuteServiceResponse mapResponse(EcService service, ExecuteServiceResponse response) {
        return response;
    }

    @Override
    public boolean supports(EcService service) {
        return false;
    }
}
