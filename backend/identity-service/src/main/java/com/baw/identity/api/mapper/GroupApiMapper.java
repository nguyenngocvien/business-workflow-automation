package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.GroupCreateRequest;
import com.baw.identity.api.dto.request.GroupUpdateRequest;
import com.baw.identity.api.dto.response.GroupResponse;
import com.baw.identity.application.port.in.GroupUseCase.CreateGroupCommand;
import com.baw.identity.application.port.in.GroupUseCase.UpdateGroupCommand;
import com.baw.identity.domain.model.Group;

public final class GroupApiMapper {

	private GroupApiMapper() {
	}

	public static CreateGroupCommand toCreateCommand(GroupCreateRequest request) {
		return new CreateGroupCommand(
			request.code(),
			request.name(),
			request.parentGroupId(),
			request.groupType(),
			request.path(),
			request.description(),
			request.source()
		);
	}

	public static UpdateGroupCommand toUpdateCommand(GroupUpdateRequest request) {
		return new UpdateGroupCommand(
			request.code(),
			request.name(),
			request.parentGroupId(),
			request.groupType(),
			request.path(),
			request.description(),
			request.source()
		);
	}

	public static GroupResponse toResponse(Group group) {
		return new GroupResponse(
			group.id(),
			group.code(),
			group.name(),
			group.parentGroupId(),
			group.groupType(),
			group.path(),
			group.description(),
			group.source(),
			group.active(),
			group.createdAt()
		);
	}
}
