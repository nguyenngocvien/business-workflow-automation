package com.baw.identity.application.port.in;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.Role;
import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.RoleType;

public interface RoleUseCase {

	Role create(CreateRoleCommand command);

	Role update(Long id, UpdateRoleCommand command);

	Optional<Role> findById(Long id);

	Optional<Role> findByCode(String code);

	List<Role> findAll();

	record CreateRoleCommand(
		String code,
		String name,
		RoleType roleType,
		String description,
		RecordSource source,
		Boolean system
	) {
	}

	record UpdateRoleCommand(
		String code,
		String name,
		RoleType roleType,
		String description,
		RecordSource source,
		Boolean system
	) {
	}
}
