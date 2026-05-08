package com.baw.identity.domain.model;

import java.time.OffsetDateTime;
import java.util.Map;

public record IdentityProvider(
	Long id,
	String providerCode,
	String providerName,
	String providerType,
	Map<String, Object> config,
	boolean active,
	OffsetDateTime createdAt
) {
}
