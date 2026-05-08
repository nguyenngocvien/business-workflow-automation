package com.baw.identity.api.dto.request;

import java.util.UUID;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
	UUID externalId,
	@NotBlank
	String username,
	String email,
	String firstName,
	String lastName,
	String fullName,
	String phoneNumber,
	@NotNull
	UserStatus status,
	@NotNull
	RecordSource source
) {
}
