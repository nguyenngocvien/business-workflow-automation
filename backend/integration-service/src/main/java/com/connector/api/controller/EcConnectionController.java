package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcConnectionRequest;
import com.connector.application.dto.response.EcConnectionResponse;
import com.connector.application.service.EcConnectionApplicationService;

@RestController
@RequestMapping("/api/connections")
public class EcConnectionController extends AbstractCrudController<EcConnectionRequest, EcConnectionResponse, Long> {

    public EcConnectionController(EcConnectionApplicationService service) {
        super(service);
    }
}
