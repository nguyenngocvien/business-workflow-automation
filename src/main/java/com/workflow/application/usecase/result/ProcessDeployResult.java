package com.workflow.application.usecase.result;

import lombok.Builder;

@Builder
public record ProcessDeployResult(
    Long processVersionId,
    String processKey,
    Integer version,
    String deploymentId,
    Long camundaProcessDefinitionKey,
    String camundaVersion,
    String message
) {
}
