package com.baw.identity.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.UserStatus;

public record User(
	Long id,
	UUID externalId,
	String username,
	String email,
	String firstName,
	String lastName,
	String fullName,
	String phoneNumber,
	UserStatus status,
	RecordSource source,
	boolean deleted,
	OffsetDateTime createdAt,
	OffsetDateTime updatedAt
) {
}
