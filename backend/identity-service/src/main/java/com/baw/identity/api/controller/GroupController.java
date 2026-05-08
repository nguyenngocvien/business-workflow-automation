package com.baw.identity.api.controller;

import java.util.List;

import com.baw.identity.api.dto.request.GroupCreateRequest;
import com.baw.identity.api.dto.request.GroupUpdateRequest;
import com.baw.identity.api.dto.response.GroupResponse;
import com.baw.identity.api.error.ResourceNotFoundException;
import com.baw.identity.api.mapper.GroupApiMapper;
import com.baw.identity.application.port.in.GroupUseCase;
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
@RequestMapping("/api/groups")
@Tag(name = "Groups", description = "Group management APIs")
public class GroupController {

	private final GroupUseCase groupUseCase;

	public GroupController(GroupUseCase groupUseCase) {
		this.groupUseCase = groupUseCase;
	}

	@PostMapping
	@Operation(summary = "Create a group", description = "Creates a new group record")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Group created"),
		@ApiResponse(responseCode = "400", description = "Validation failed")
	})
	public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupCreateRequest request) {
		GroupResponse response = GroupApiMapper.toResponse(groupUseCase.create(GroupApiMapper.toCreateCommand(request)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a group", description = "Updates an existing group record")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Group updated"),
		@ApiResponse(responseCode = "400", description = "Validation failed"),
		@ApiResponse(responseCode = "404", description = "Group not found")
	})
	public ResponseEntity<GroupResponse> update(
		@Parameter(description = "Group identifier")
		@PathVariable Long id,
		@Valid @RequestBody GroupUpdateRequest request
	) {
		return ResponseEntity.ok(GroupApiMapper.toResponse(groupUseCase.update(id, GroupApiMapper.toUpdateCommand(request))));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a group by ID", description = "Returns a single group by identifier")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Group found"),
		@ApiResponse(responseCode = "404", description = "Group not found")
	})
	public ResponseEntity<GroupResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(GroupApiMapper.toResponse(
			groupUseCase.findById(id).orElseThrow(() -> new ResourceNotFoundException("Group not found: " + id))
		));
	}

	@GetMapping
	@Operation(summary = "List groups", description = "Returns all groups")
	public ResponseEntity<List<GroupResponse>> findAll() {
		List<GroupResponse> response = groupUseCase.findAll().stream()
			.map(GroupApiMapper::toResponse)
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/code/{code}")
	@Operation(summary = "Get a group by code", description = "Returns a single group by code")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Group found"),
		@ApiResponse(responseCode = "404", description = "Group not found")
	})
	public ResponseEntity<GroupResponse> findByCode(@PathVariable String code) {
		return ResponseEntity.ok(GroupApiMapper.toResponse(
			groupUseCase.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Group not found: " + code))
		));
	}
}
