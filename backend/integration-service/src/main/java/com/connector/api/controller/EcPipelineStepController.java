package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcPipelineStepRequest;
import com.connector.application.dto.response.EcPipelineStepResponse;
import com.connector.application.service.EcPipelineStepApplicationService;

@RestController
@RequestMapping("/api/pipeline-steps")
public class EcPipelineStepController
    extends AbstractCrudController<EcPipelineStepRequest, EcPipelineStepResponse, Long> {

    public EcPipelineStepController(EcPipelineStepApplicationService service) {
        super(service);
    }
}
