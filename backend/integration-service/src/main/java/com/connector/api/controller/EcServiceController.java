package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcServiceRequest;
import com.connector.application.dto.response.EcServiceResponse;
import com.connector.application.service.EcServiceApplicationService;

@RestController
@RequestMapping("/api/services")
public class EcServiceController extends AbstractCrudController<EcServiceRequest, EcServiceResponse, Long> {

    public EcServiceController(EcServiceApplicationService service) {
        super(service);
    }
}
