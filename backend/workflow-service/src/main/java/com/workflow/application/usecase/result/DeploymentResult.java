package com.workflow.application.usecase.result;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeploymentResult {
    private String deploymentKey;
    private long processDefinitionKey;
    private String camundaVersion;
}