package com.baw.identity.api.dto.request;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IdentityProviderCreateRequest(
	@NotBlank
	String providerCode,
	@NotBlank
	String providerName,
	@NotBlank
	String providerType,
	@NotNull
	Map<String, Object> config,
	Boolean active
) {
}
