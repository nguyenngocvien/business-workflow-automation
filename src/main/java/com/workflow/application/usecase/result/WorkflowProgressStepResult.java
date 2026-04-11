package com.workflow.application.usecase.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WorkflowProgressStepResult(
    Integer stepOrder,
    String stepCode,
    String stepName,
    String stepType,
    String processor,
    @Schema(description = "Full name of the processor resolved from wf_user.full_name", example = "Tran Thi B")
    String processorFullName,
    String status,
    boolean currentStep,
    LocalDateTime startedAt,
    LocalDateTime endedAt
) {
}
