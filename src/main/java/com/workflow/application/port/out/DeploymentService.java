package com.workflow.application.port.out;

import com.workflow.application.usecase.result.DeploymentResult;

public interface DeploymentService {
    DeploymentResult deployBpmn(String processKey, Integer version, String bpmnXml);
}
