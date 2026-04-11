package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowDefinitionDeploymentResult(
    Long id,
    Long workflowDefinitionId,
    String workflowKey,
    Integer definitionVersion,
    String deploymentStatus,
    String camundaDeploymentId,
    String camundaProcessDefinitionId,
    String camundaProcessDefinitionKey,
    Integer camundaProcessDefinitionVersion,
    LocalDateTime createdAt,
    LocalDateTime deployedAt,
    String deployedBy,
    String failureReason
) {
}
