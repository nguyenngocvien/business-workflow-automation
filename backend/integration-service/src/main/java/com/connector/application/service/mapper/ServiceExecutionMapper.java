package com.connector.application.service.mapper;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ServiceType;

public interface ServiceExecutionMapper {

    ServiceType supportedType();

    String supportedServiceCode();

    ExecuteServiceRequest mapRequest(EcService service, ExecuteServiceRequest request);

    ExecuteServiceResponse mapResponse(EcService service, ExecuteServiceResponse response);

    default boolean supports(EcService service) {
        if (service == null || service.getServiceType() == null || service.getServiceCode() == null) {
            return false;
        }
        return supportedType() == service.getServiceType()
            && supportedServiceCode().equalsIgnoreCase(service.getServiceCode());
    }
}
