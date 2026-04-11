package com.connector.api.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.service.config.ServiceConfigSupportService;
import com.connector.domain.enums.ServiceType;

@RestController
@RequestMapping("/api/service-config-schemas")
public class ServiceConfigSchemaController {

    private final ServiceConfigSupportService serviceConfigSupportService;

    public ServiceConfigSchemaController(ServiceConfigSupportService serviceConfigSupportService) {
        this.serviceConfigSupportService = serviceConfigSupportService;
    }

    @GetMapping("/{serviceType}")
    public Map<String, Object> getSchema(@PathVariable ServiceType serviceType) {
        return serviceConfigSupportService.schema(serviceType);
    }
}
