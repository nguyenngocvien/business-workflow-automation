package com.baw.identity.api.controller;

import java.util.List;

import com.baw.identity.api.dto.request.AddUserGroupRequest;
import com.baw.identity.api.dto.response.UserGroupResponse;
import com.baw.identity.api.mapper.UserGroupApiMapper;
import com.baw.identity.application.port.in.UserGroupUseCase;
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
@RequestMapping("/api/user-groups")
@Tag(name = "User Groups", description = "User-to-group membership APIs")
public class UserGroupController {

	private final UserGroupUseCase userGroupUseCase;

	public UserGroupController(UserGroupUseCase userGroupUseCase) {
		this.userGroupUseCase = userGroupUseCase;
	}

	@PostMapping
	@Operation(summary = "Add a user to a group", description = "Creates a user-group membership")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Membership created"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "User or group not found")
	})
	public ResponseEntity<UserGroupResponse> add(@Valid @RequestBody AddUserGroupRequest request) {
		UserGroupResponse response = UserGroupApiMapper.toResponse(
			userGroupUseCase.add(UserGroupApiMapper.toCommand(request))
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{userId}/{groupId}")
	@Operation(summary = "Remove a user from a group", description = "Deletes a user-group membership")
	public ResponseEntity<Void> remove(@PathVariable Long userId, @PathVariable Long groupId) {
		userGroupUseCase.remove(userId, groupId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{userId}/{groupId}")
	@Operation(summary = "Get a user-group membership", description = "Returns a single membership by user and group")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Membership found"),
		@ApiResponse(responseCode = "404", description = "Membership not found")
	})
	public ResponseEntity<UserGroupResponse> find(@PathVariable Long userId, @PathVariable Long groupId) {
		return userGroupUseCase.find(userId, groupId)
			.map(UserGroupApiMapper::toResponse)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "List groups for a user", description = "Returns all group memberships for the given user")
	public ResponseEntity<List<UserGroupResponse>> findByUserId(@PathVariable Long userId) {
		List<UserGroupResponse> response = userGroupUseCase.findByUserId(userId).stream()
			.map(UserGroupApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}
}
