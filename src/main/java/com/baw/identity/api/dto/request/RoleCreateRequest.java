package com.baw.identity.api.dto.request;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleCreateRequest(
	@NotBlank
	String code,
	@NotBlank
	String name,
	@NotNull
	RoleType roleType,
	String description,
	@NotNull
	RecordSource source,
	Boolean system
) {
}
