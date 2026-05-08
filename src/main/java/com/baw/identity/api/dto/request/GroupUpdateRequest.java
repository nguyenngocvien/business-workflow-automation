package com.baw.identity.api.dto.request;

import com.baw.identity.domain.model.enums.GroupType;
import com.baw.identity.domain.model.enums.RecordSource;

public record GroupUpdateRequest(
	String code,
	String name,
	Long parentGroupId,
	GroupType groupType,
	String path,
	String description,
	RecordSource source
) {
}
