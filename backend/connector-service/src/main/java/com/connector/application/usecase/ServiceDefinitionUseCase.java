package com.connector.application.usecase;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreateServiceCommand;
import com.connector.application.command.UpdateServiceCommand;
import com.connector.application.result.ServiceResult;
import com.connector.domain.enums.ServiceType;

public interface ServiceDefinitionUseCase {

    Page<ServiceResult> findAll(Pageable pageable);

    ServiceResult findById(Long id);

    ServiceResult create(CreateServiceCommand command);

    ServiceResult update(UpdateServiceCommand command);

    void delete(Long id);

    Map<String, Object> schema(ServiceType serviceType);
}
