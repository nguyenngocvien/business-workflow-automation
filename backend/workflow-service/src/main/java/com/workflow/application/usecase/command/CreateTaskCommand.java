package com.workflow.application.usecase.command;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;

@Builder
public record CreateTaskCommand(
        Long taskId,
        Long processInstanceId,
        String taskName,
        String taskCode,
        String assignee,
        String candidateGroup,
        String owner,
        String status,
        Integer priority,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        Map<String, Object> data) {
}