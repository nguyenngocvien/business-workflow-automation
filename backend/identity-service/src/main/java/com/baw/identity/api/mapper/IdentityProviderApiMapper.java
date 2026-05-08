package com.baw.identity.api.mapper;

import com.baw.identity.api.dto.request.IdentityProviderCreateRequest;
import com.baw.identity.api.dto.request.IdentityProviderUpdateRequest;
import com.baw.identity.api.dto.response.IdentityProviderResponse;
import com.baw.identity.application.port.in.IdentityProviderUseCase.CreateIdentityProviderCommand;
import com.baw.identity.application.port.in.IdentityProviderUseCase.UpdateIdentityProviderCommand;
import com.baw.identity.domain.model.IdentityProvider;

public final class IdentityProviderApiMapper {

	private IdentityProviderApiMapper() {
	}

	public static CreateIdentityProviderCommand toCreateCommand(IdentityProviderCreateRequest request) {
		return new CreateIdentityProviderCommand(
			request.providerCode(),
			request.providerName(),
			request.providerType(),
			request.config(),
			request.active()
		);
	}

	public static UpdateIdentityProviderCommand toUpdateCommand(IdentityProviderUpdateRequest request) {
		return new UpdateIdentityProviderCommand(
			request.providerCode(),
			request.providerName(),
			request.providerType(),
			request.config(),
			request.active()
		);
	}

	public static IdentityProviderResponse toResponse(IdentityProvider provider) {
		return new IdentityProviderResponse(
			provider.id(),
			provider.providerCode(),
			provider.providerName(),
			provider.providerType(),
			provider.config(),
			provider.active(),
			provider.createdAt()
		);
	}
}
