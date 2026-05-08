package com.baw.identity.api.controller;

import java.util.List;
import java.util.UUID;

import com.baw.identity.api.dto.request.UserCreateRequest;
import com.baw.identity.api.dto.request.UserUpdateRequest;
import com.baw.identity.api.dto.response.UserResponse;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.api.mapper.UserApiMapper;
import com.baw.identity.application.port.in.UserUseCase;
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
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

	private final UserUseCase userUseCase;

	public UserController(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	@PostMapping
	@Operation(summary = "Create a user", description = "Creates a new user record")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "User created"),
		@ApiResponse(responseCode = "400", description = "Validation failed")
	})
	public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
		UserResponse response = UserApiMapper.toResponse(userUseCase.create(UserApiMapper.toCreateCommand(request)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a user", description = "Updates an existing user record")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User updated"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	public ResponseEntity<UserResponse> update(
		@Parameter(description = "User identifier")
		@PathVariable Long id,
		@Valid @RequestBody UserUpdateRequest request
	) {
		return ResponseEntity.ok(UserApiMapper.toResponse(userUseCase.update(id, UserApiMapper.toUpdateCommand(request))));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a user by ID", description = "Returns a single user by identifier")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User found"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(UserApiMapper.toResponse(
			userUseCase.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id))
		));
	}

	@GetMapping
	@Operation(summary = "List users", description = "Returns all users")
	public ResponseEntity<List<UserResponse>> findAll() {
		List<UserResponse> response = userUseCase.findAll().stream()
			.map(UserApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/username/{username}")
	@Operation(summary = "Get a user by username", description = "Returns a single user by username")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User found"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	public ResponseEntity<UserResponse> findByUsername(@PathVariable String username) {
		return ResponseEntity.ok(UserApiMapper.toResponse(
			userUseCase.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username))
		));
	}

	@GetMapping("/external/{externalId}")
	@Operation(summary = "Get a user by external ID", description = "Returns a single user by external identifier")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User found"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	public ResponseEntity<UserResponse> findByExternalId(@PathVariable UUID externalId) {
		return ResponseEntity.ok(UserApiMapper.toResponse(
			userUseCase.findByExternalId(externalId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + externalId))
		));
	}

	@PutMapping("/{id}/deactivate")
	@Operation(summary = "Deactivate a user", description = "Marks a user as inactive and deleted")
	public ResponseEntity<UserResponse> deactivate(@PathVariable Long id) {
		return ResponseEntity.ok(UserApiMapper.toResponse(userUseCase.deactivate(id)));
	}
}
