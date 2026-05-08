package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.RoleCreateRequest;
import com.baw.identity.api.dto.request.RoleUpdateRequest;
import com.baw.identity.api.dto.response.RoleResponse;
import com.baw.identity.application.port.in.RoleUseCase.CreateRoleCommand;
import com.baw.identity.application.port.in.RoleUseCase.UpdateRoleCommand;
import com.baw.identity.domain.model.Role;

public final class RoleApiMapper {

	private RoleApiMapper() {
	}

	public static CreateRoleCommand toCreateCommand(RoleCreateRequest request) {
		return new CreateRoleCommand(
			request.code(),
			request.name(),
			request.roleType(),
			request.description(),
			request.source(),
			request.system()
		);
	}

	public static UpdateRoleCommand toUpdateCommand(RoleUpdateRequest request) {
		return new UpdateRoleCommand(
			request.code(),
			request.name(),
			request.roleType(),
			request.description(),
			request.source(),
			request.system()
		);
	}

	public static RoleResponse toResponse(Role role) {
		return new RoleResponse(
			role.id(),
			role.code(),
			role.name(),
			role.roleType(),
			role.description(),
			role.source(),
			role.system(),
			role.createdAt()
		);
	}
}
