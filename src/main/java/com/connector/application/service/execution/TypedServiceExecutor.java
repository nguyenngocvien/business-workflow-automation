package com.connector.application.service.execution;

import com.connector.application.dto.request.ExecuteServiceRequest;
import com.connector.application.dto.response.ExecuteServiceResponse;
import com.connector.domain.entity.EcService;
import com.connector.domain.enums.ServiceType;

public interface TypedServiceExecutor {

    ServiceType supportedType();

    ExecuteServiceResponse execute(EcService service, ExecuteServiceRequest request);
}
