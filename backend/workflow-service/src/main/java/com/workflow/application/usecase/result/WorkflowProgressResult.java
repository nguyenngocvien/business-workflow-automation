package com.workflow.application.usecase.result;

import java.util.List;
import lombok.Builder;

@Builder
public record WorkflowProgressResult(
    Long workflowInstanceId,
    String businessKey,
    String workflowStatus,
    String currentStepCode,
    Integer totalSteps,
    List<WorkflowProgressStepResult> steps
) {
}
