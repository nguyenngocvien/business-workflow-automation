package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.Role;

public interface RoleRepositoryPort {

	Role save(Role role);

	Optional<Role> findById(Long id);

	Optional<Role> findByCode(String code);

	List<Role> findAll();
}
