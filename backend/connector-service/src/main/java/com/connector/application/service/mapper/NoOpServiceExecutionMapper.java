package com.connector.application.service.mapper;

import org.springframework.stereotype.Component;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.domain.entity.ServiceEntity;
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
    public ExecuteServiceCommand mapRequest(ServiceEntity service, ExecuteServiceCommand request) {
        return request;
    }

    @Override
    public ExecuteServiceResult mapResponse(ServiceEntity service, ExecuteServiceResult response) {
        return response;
    }

    @Override
    public boolean supports(ServiceEntity service) {
        return false;
    }
}
