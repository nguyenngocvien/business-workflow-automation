package com.baw.identity.api.dto.request;

import java.util.Map;

public record IdentityProviderUpdateRequest(
	String providerCode,
	String providerName,
	String providerType,
	Map<String, Object> config,
	Boolean active
) {
}
