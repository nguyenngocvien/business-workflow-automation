package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.GrantRolePermissionRequest;
import com.baw.identity.api.dto.response.RolePermissionResponse;
import com.baw.identity.application.port.in.RolePermissionUseCase.GrantRolePermissionCommand;
import com.baw.identity.domain.model.RolePermissionGrant;

public final class RolePermissionApiMapper {

	private RolePermissionApiMapper() {
	}

	public static GrantRolePermissionCommand toCommand(GrantRolePermissionRequest request) {
		return new GrantRolePermissionCommand(
			request.roleId(),
			request.permissionId()
		);
	}

	public static RolePermissionResponse toResponse(RolePermissionGrant grant) {
		return new RolePermissionResponse(
			grant.roleId(),
			grant.permissionId()
		);
	}
}
