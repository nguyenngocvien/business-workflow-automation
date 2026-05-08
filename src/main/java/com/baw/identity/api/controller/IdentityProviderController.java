package com.baw.identity.api.controller;

import java.util.List;

import com.baw.identity.api.dto.request.IdentityProviderCreateRequest;
import com.baw.identity.api.dto.request.IdentityProviderUpdateRequest;
import com.baw.identity.api.dto.response.IdentityProviderResponse;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.api.mapper.IdentityProviderApiMapper;
import com.baw.identity.application.port.in.IdentityProviderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/identity-providers")
@Tag(name = "Identity Providers", description = "Identity provider management APIs")
public class IdentityProviderController {

	private final IdentityProviderUseCase identityProviderUseCase;

	public IdentityProviderController(IdentityProviderUseCase identityProviderUseCase) {
		this.identityProviderUseCase = identityProviderUseCase;
	}

	@PostMapping
	@Operation(summary = "Create an identity provider", description = "Creates a new identity provider record")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Identity provider created"),
		@ApiResponse(responseCode = "400", description = "Validation failed")
	})
	public ResponseEntity<IdentityProviderResponse> create(@Valid @RequestBody IdentityProviderCreateRequest request) {
		IdentityProviderResponse response = IdentityProviderApiMapper.toResponse(
			identityProviderUseCase.create(IdentityProviderApiMapper.toCreateCommand(request))
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an identity provider", description = "Updates an existing identity provider record")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Identity provider updated"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "Identity provider not found")
	})
	public ResponseEntity<IdentityProviderResponse> update(
		@Parameter(description = "Identity provider identifier")
		@PathVariable Long id,
		@Valid @RequestBody IdentityProviderUpdateRequest request
	) {
		return ResponseEntity.ok(IdentityProviderApiMapper.toResponse(
			identityProviderUseCase.update(id, IdentityProviderApiMapper.toUpdateCommand(request))
		));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get an identity provider by ID", description = "Returns a single identity provider by identifier")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Identity provider found"),
		@ApiResponse(responseCode = "404", description = "Identity provider not found")
	})
	public ResponseEntity<IdentityProviderResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(IdentityProviderApiMapper.toResponse(
			identityProviderUseCase.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Identity provider not found: " + id))
		));
	}

	@GetMapping
	@Operation(summary = "List identity providers", description = "Returns all identity providers")
	public ResponseEntity<List<IdentityProviderResponse>> findAll() {
		List<IdentityProviderResponse> response = identityProviderUseCase.findAll().stream()
			.map(IdentityProviderApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/code/{providerCode}")
	@Operation(summary = "Get an identity provider by code", description = "Returns a single identity provider by provider code")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Identity provider found"),
		@ApiResponse(responseCode = "404", description = "Identity provider not found")
	})
	public ResponseEntity<IdentityProviderResponse> findByProviderCode(@PathVariable String providerCode) {
		return ResponseEntity.ok(IdentityProviderApiMapper.toResponse(
			identityProviderUseCase.findByProviderCode(providerCode)
				.orElseThrow(() -> new ResourceNotFoundException("Identity provider not found: " + providerCode))
		));
	}
}
