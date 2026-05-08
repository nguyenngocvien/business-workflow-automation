package com.connector.application.service.executor;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.result.ExecuteServiceResult;
import com.connector.domain.entity.ServiceEntity;
import com.connector.domain.enums.ServiceType;

public interface ServiceExecutor {

    ServiceType supportedType();

    ExecuteServiceResult execute(ServiceEntity service, ExecuteServiceCommand request);
}
