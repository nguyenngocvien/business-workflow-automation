package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.AssignUserRoleRequest;
import com.baw.identity.api.dto.response.UserRoleResponse;
import com.baw.identity.application.port.in.UserRoleUseCase.AssignUserRoleCommand;
import com.baw.identity.domain.model.UserRoleAssignment;

public final class UserRoleApiMapper {

	private UserRoleApiMapper() {
	}

	public static AssignUserRoleCommand toCommand(AssignUserRoleRequest request) {
		return new AssignUserRoleCommand(
			request.userId(),
			request.roleId(),
			request.expiresAt()
		);
	}

	public static UserRoleResponse toResponse(UserRoleAssignment assignment) {
		return new UserRoleResponse(
			assignment.userId(),
			assignment.roleId(),
			assignment.assignedAt(),
			assignment.expiresAt()
		);
	}
}
