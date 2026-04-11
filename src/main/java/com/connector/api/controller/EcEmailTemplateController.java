package com.connector.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.api.common.AbstractCrudController;
import com.connector.application.dto.request.EcEmailTemplateRequest;
import com.connector.application.dto.response.EcEmailTemplateResponse;
import com.connector.application.service.EcEmailTemplateApplicationService;

@RestController
@RequestMapping("/api/email-templates")
public class EcEmailTemplateController
    extends AbstractCrudController<EcEmailTemplateRequest, EcEmailTemplateResponse, Long> {

    public EcEmailTemplateController(EcEmailTemplateApplicationService service) {
        super(service);
    }
}
