package com.baw.identity.domain.model;

import java.time.OffsetDateTime;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.RoleType;

public record Role(
	Long id,
	String code,
	String name,
	RoleType roleType,
	String description,
	RecordSource source,
	boolean system,
	OffsetDateTime createdAt
) {
}
