package com.baw.identity.api.dto.request;

import java.util.UUID;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.UserStatus;

public record UserUpdateRequest(
	UUID externalId,
	String username,
	String email,
	String firstName,
	String lastName,
	String fullName,
	String phoneNumber,
	UserStatus status,
	RecordSource source
) {
}
