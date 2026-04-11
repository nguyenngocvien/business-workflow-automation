package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcPipelineRequest;
import com.connector.application.dto.response.EcPipelineResponse;
import com.connector.application.service.EcPipelineApplicationService;

@RestController
@RequestMapping("/api/pipelines")
public class EcPipelineController extends AbstractCrudController<EcPipelineRequest, EcPipelineResponse, Long> {

    public EcPipelineController(EcPipelineApplicationService service) {
        super(service);
    }
}
