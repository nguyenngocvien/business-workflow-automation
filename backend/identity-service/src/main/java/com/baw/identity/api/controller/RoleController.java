package com.baw.identity.api.controller;

import java.util.List;

import com.baw.identity.api.dto.request.RoleCreateRequest;
import com.baw.identity.api.dto.request.RoleUpdateRequest;
import com.baw.identity.api.dto.response.RoleResponse;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.api.mapper.RoleApiMapper;
import com.baw.identity.application.port.in.RoleUseCase;
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
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Role management APIs")
public class RoleController {

	private final RoleUseCase roleUseCase;

	public RoleController(RoleUseCase roleUseCase) {
		this.roleUseCase = roleUseCase;
	}

	@PostMapping
	@Operation(summary = "Create a role", description = "Creates a new role record")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Role created"),
		@ApiResponse(responseCode = "400", description = "Validation failed")
	})
	public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleCreateRequest request) {
		RoleResponse response = RoleApiMapper.toResponse(roleUseCase.create(RoleApiMapper.toCreateCommand(request)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a role", description = "Updates an existing role record")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Role updated"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "Role not found")
	})
	public ResponseEntity<RoleResponse> update(
		@Parameter(description = "Role identifier")
		@PathVariable Long id,
		@Valid @RequestBody RoleUpdateRequest request
	) {
		return ResponseEntity.ok(RoleApiMapper.toResponse(roleUseCase.update(id, RoleApiMapper.toUpdateCommand(request))));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a role by ID", description = "Returns a single role by identifier")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Role found"),
		@ApiResponse(responseCode = "404", description = "Role not found")
	})
	public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(RoleApiMapper.toResponse(
			roleUseCase.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id))
		));
	}

	@GetMapping
	@Operation(summary = "List roles", description = "Returns all roles")
	public ResponseEntity<List<RoleResponse>> findAll() {
		List<RoleResponse> response = roleUseCase.findAll().stream()
			.map(RoleApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/code/{code}")
	@Operation(summary = "Get a role by code", description = "Returns a single role by code")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Role found"),
		@ApiResponse(responseCode = "404", description = "Role not found")
	})
	public ResponseEntity<RoleResponse> findByCode(@PathVariable String code) {
		return ResponseEntity.ok(RoleApiMapper.toResponse(
			roleUseCase.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + code))
		));
	}
}
