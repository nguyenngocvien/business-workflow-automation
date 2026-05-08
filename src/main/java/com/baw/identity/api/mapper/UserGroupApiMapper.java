package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.AddUserGroupRequest;
import com.baw.identity.api.dto.response.UserGroupResponse;
import com.baw.identity.application.port.in.UserGroupUseCase.AddUserGroupCommand;
import com.baw.identity.domain.model.UserGroupMembership;

public final class UserGroupApiMapper {

	private UserGroupApiMapper() {
	}

	public static AddUserGroupCommand toCommand(AddUserGroupRequest request) {
		return new AddUserGroupCommand(
			request.userId(),
			request.groupId()
		);
	}

	public static UserGroupResponse toResponse(UserGroupMembership membership) {
		return new UserGroupResponse(
			membership.userId(),
			membership.groupId(),
			membership.joinedAt()
		);
	}
}
