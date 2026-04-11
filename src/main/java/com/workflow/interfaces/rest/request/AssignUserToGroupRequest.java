package com.workflow.interfaces.rest.request;

import jakarta.validation.constraints.NotNull;

public record AssignUserToGroupRequest(
    @NotNull Long userId,
    @NotNull Long groupId
) {
}
