package com.baw.identity.api.controller;

import java.util.List;

import com.baw.identity.api.dto.request.AssignUserRoleRequest;
import com.baw.identity.api.dto.response.UserRoleResponse;
import com.baw.identity.api.mapper.UserRoleApiMapper;
import com.baw.identity.application.port.in.UserRoleUseCase;
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
@RequestMapping("/api/user-roles")
@Tag(name = "User Roles", description = "User-to-role assignment APIs")
public class UserRoleController {

	private final UserRoleUseCase userRoleUseCase;

	public UserRoleController(UserRoleUseCase userRoleUseCase) {
		this.userRoleUseCase = userRoleUseCase;
	}

	@PostMapping
	@Operation(summary = "Assign a role to a user", description = "Creates a user-role assignment")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Assignment created"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "User or role not found")
	})
	public ResponseEntity<UserRoleResponse> assign(@Valid @RequestBody AssignUserRoleRequest request) {
		UserRoleResponse response = UserRoleApiMapper.toResponse(
			userRoleUseCase.assign(UserRoleApiMapper.toCommand(request))
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{userId}/{roleId}")
	@Operation(summary = "Revoke a user role", description = "Deletes a user-role assignment")
	public ResponseEntity<Void> revoke(@PathVariable Long userId, @PathVariable Long roleId) {
		userRoleUseCase.revoke(userId, roleId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{userId}/{roleId}")
	@Operation(summary = "Get a user-role assignment", description = "Returns a single assignment by user and role")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Assignment found"),
		@ApiResponse(responseCode = "404", description = "Assignment not found")
	})
	public ResponseEntity<UserRoleResponse> find(@PathVariable Long userId, @PathVariable Long roleId) {
		return userRoleUseCase.find(userId, roleId)
			.map(UserRoleApiMapper::toResponse)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "List roles for a user", description = "Returns all role assignments for the given user")
	public ResponseEntity<List<UserRoleResponse>> findByUserId(@PathVariable Long userId) {
		List<UserRoleResponse> response = userRoleUseCase.findByUserId(userId).stream()
			.map(UserRoleApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}
}
