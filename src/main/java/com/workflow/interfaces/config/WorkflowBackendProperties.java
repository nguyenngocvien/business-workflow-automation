package com.workflow.interfaces.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "workflow")
public class WorkflowBackendProperties {

    private Provider provider = Provider.CAMUNDA;

    public enum Provider {
        CAMUNDA,
        IBM
    }
}
