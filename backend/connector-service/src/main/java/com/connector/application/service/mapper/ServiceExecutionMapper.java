package com.connector.application.service.mapper;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;

public interface ServiceExecutionMapper {

    ServiceType supportedType();

    String supportedServiceCode();

    ExecuteServiceCommand mapRequest(ServiceEntity service, ExecuteServiceCommand request);

    ExecuteServiceResult mapResponse(ServiceEntity service, ExecuteServiceResult response);

    default boolean supports(ServiceEntity service) {
        if (service == null || service.getServiceType() == null || service.getServiceCode() == null) {
            return false;
        }
        return supportedType() == service.getServiceType()
            && supportedServiceCode().equalsIgnoreCase(service.getServiceCode());
    }
}
