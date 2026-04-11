package com.workflow.application.usecase.command;

import java.util.Map;

import lombok.Builder;

@Builder
public record CreateProcessVersionCommand(
    String processKey,
    String comment,
    Map<String, Object> data
) {
}