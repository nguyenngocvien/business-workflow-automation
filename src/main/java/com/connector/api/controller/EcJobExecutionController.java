package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcJobExecutionRequest;
import com.connector.application.dto.response.EcJobExecutionResponse;
import com.connector.application.service.EcJobExecutionApplicationService;

@RestController
@RequestMapping("/api/job-executions")
public class EcJobExecutionController
    extends AbstractCrudController<EcJobExecutionRequest, EcJobExecutionResponse, Long> {

    public EcJobExecutionController(EcJobExecutionApplicationService service) {
        super(service);
    }
}
