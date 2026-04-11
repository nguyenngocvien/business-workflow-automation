package com.workflow.application.usecase.result;

import lombok.Builder;
import java.time.LocalDateTime;

import com.workflow.domain.enums.DeployStatus;

@Builder
public record ProcessVersionResult(
    Long id,
    String processKey,
    Integer version,
    String name,
    String description,
    DeployStatus status,
    Long camundaProcessDefinitionKey,
    String camundaVersion,
    String deploymentId,
    String createdBy,
    LocalDateTime createdAt,
    LocalDateTime deployedAt
) {
}