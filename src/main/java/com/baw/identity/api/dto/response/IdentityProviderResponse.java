package com.baw.identity.api.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

public record IdentityProviderResponse(
	Long id,
	String providerCode,
	String providerName,
	String providerType,
	Map<String, Object> config,
	boolean active,
	OffsetDateTime createdAt
) {
}
