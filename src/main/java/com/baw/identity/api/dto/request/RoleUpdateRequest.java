package com.baw.identity.api.dto.request;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.RoleType;

public record RoleUpdateRequest(
	String code,
	String name,
	RoleType roleType,
	String description,
	RecordSource source,
	Boolean system
) {
}
