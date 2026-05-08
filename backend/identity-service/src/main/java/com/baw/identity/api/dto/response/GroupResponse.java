package com.baw.identity.api.dto.response;

import java.time.OffsetDateTime;

import com.baw.identity.domain.model.enums.GroupType;
import com.baw.identity.domain.model.enums.RecordSource;

public record GroupResponse(
	Long id,
	String code,
	String name,
	Long parentGroupId,
	GroupType groupType,
	String path,
	String description,
	RecordSource source,
	boolean active,
	OffsetDateTime createdAt
) {
}
