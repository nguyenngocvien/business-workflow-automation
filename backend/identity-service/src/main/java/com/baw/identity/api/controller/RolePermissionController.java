package com.baw.identity.api.controller;

import java.util.List;

import com.baw.identity.api.dto.request.GrantRolePermissionRequest;
import com.baw.identity.api.dto.response.RolePermissionResponse;
import com.baw.identity.api.mapper.RolePermissionApiMapper;
import com.baw.identity.application.port.in.RolePermissionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/role-permissions")
@Tag(name = "Role Permissions", description = "Role-to-permission grant APIs")
public class RolePermissionController {

	private final RolePermissionUseCase rolePermissionUseCase;

	public RolePermissionController(RolePermissionUseCase rolePermissionUseCase) {
		this.rolePermissionUseCase = rolePermissionUseCase;
	}

	@PostMapping
	@Operation(summary = "Grant a permission to a role", description = "Creates a role-permission grant")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Grant created"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "Role or permission not found")
	})
	public ResponseEntity<RolePermissionResponse> grant(@Valid @RequestBody GrantRolePermissionRequest request) {
		RolePermissionResponse response = RolePermissionApiMapper.toResponse(
			rolePermissionUseCase.grant(RolePermissionApiMapper.toCommand(request))
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{roleId}/{permissionId}")
	@Operation(summary = "Revoke a role permission", description = "Deletes a role-permission grant")
	public ResponseEntity<Void> revoke(@PathVariable Long roleId, @PathVariable Long permissionId) {
		rolePermissionUseCase.revoke(roleId, permissionId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{roleId}/{permissionId}")
	@Operation(summary = "Get a role-permission grant", description = "Returns a single grant by role and permission")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Grant found"),
		@ApiResponse(responseCode = "404", description = "Grant not found")
	})
	public ResponseEntity<RolePermissionResponse> find(@PathVariable Long roleId, @PathVariable Long permissionId) {
		return rolePermissionUseCase.find(roleId, permissionId)
			.map(RolePermissionApiMapper::toResponse)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/role/{roleId}")
	@Operation(summary = "List permissions for a role", description = "Returns all permission grants for the given role")
	public ResponseEntity<List<RolePermissionResponse>> findByRoleId(@PathVariable Long roleId) {
		List<RolePermissionResponse> response = rolePermissionUseCase.findByRoleId(roleId).stream()
			.map(RolePermissionApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}
}
