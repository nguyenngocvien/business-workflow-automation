package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.Permission;

public interface PermissionRepositoryPort {

	Permission save(Permission permission);

	Optional<Permission> findById(Long id);

	Optional<Permission> findByCode(String code);

	List<Permission> findAll();
}
