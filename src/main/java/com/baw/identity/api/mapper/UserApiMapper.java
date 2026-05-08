package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.UserCreateRequest;
import com.baw.identity.api.dto.request.UserUpdateRequest;
import com.baw.identity.api.dto.response.UserResponse;
import com.baw.identity.application.port.in.UserUseCase.CreateUserCommand;
import com.baw.identity.application.port.in.UserUseCase.UpdateUserCommand;
import com.baw.identity.domain.model.User;

public final class UserApiMapper {

	private UserApiMapper() {
	}

	public static CreateUserCommand toCreateCommand(UserCreateRequest request) {
		return new CreateUserCommand(
			request.externalId(),
			request.username(),
			request.email(),
			request.firstName(),
			request.lastName(),
			request.fullName(),
			request.phoneNumber(),
			request.status(),
			request.source()
		);
	}

	public static UpdateUserCommand toUpdateCommand(UserUpdateRequest request) {
		return new UpdateUserCommand(
			request.externalId(),
			request.username(),
			request.email(),
			request.firstName(),
			request.lastName(),
			request.fullName(),
			request.phoneNumber(),
			request.status(),
			request.source()
		);
	}

	public static UserResponse toResponse(User user) {
		return new UserResponse(
			user.id(),
			user.externalId(),
			user.username(),
			user.email(),
			user.firstName(),
			user.lastName(),
			user.fullName(),
			user.phoneNumber(),
			user.status(),
			user.source(),
			user.deleted(),
			user.createdAt(),
			user.updatedAt()
		);
	}
}
