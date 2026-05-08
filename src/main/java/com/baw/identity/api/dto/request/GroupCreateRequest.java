package com.baw.identity.api.dto.request;

import com.baw.identity.domain.model.enums.GroupType;
import com.baw.identity.domain.model.enums.RecordSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupCreateRequest(
	@NotBlank
	String code,
	@NotBlank
	String name,
	Long parentGroupId,
	@NotNull
	GroupType groupType,
	String path,
	String description,
	@NotNull
	RecordSource source
) {
}
