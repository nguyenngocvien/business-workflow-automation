package com.workflow.application.usecase.result;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowBpmnResult(
    Long id,
    Long workflowDefinitionId,
    String workflowKey,
    Integer definitionVersion,
    String resourceName,
    String deploymentStatus,
    String bpmnChecksum,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy,
    LocalDateTime deployedAt,
    String deployedBy
) {
}
