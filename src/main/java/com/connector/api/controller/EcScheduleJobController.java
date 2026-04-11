package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcScheduleJobRequest;
import com.connector.application.dto.response.EcScheduleJobResponse;
import com.connector.application.service.EcScheduleJobApplicationService;

@RestController
@RequestMapping("/api/schedule-jobs")
public class EcScheduleJobController
    extends AbstractCrudController<EcScheduleJobRequest, EcScheduleJobResponse, Long> {

    public EcScheduleJobController(EcScheduleJobApplicationService service) {
        super(service);
    }
}
