package com.baw.identity.domain.model;

import java.time.OffsetDateTime;

public record Permission(
	Long id,
	String code,
	String name,
	String resource,
	String action,
	OffsetDateTime createdAt
) {
}
