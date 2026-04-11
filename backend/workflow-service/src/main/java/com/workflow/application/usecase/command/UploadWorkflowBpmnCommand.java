package com.workflow.application.usecase.command;

import lombok.Builder;

@Builder
public record UploadWorkflowBpmnCommand(
    Long definitionId,
    String resourceName,
    String bpmnXml,
    String uploadedBy
) {
}
