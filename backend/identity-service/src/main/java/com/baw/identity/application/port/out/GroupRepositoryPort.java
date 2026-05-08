package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;

import com.baw.identity.domain.model.Group;

public interface GroupRepositoryPort {

	Group save(Group group);

	Optional<Group> findById(Long id);

	Optional<Group> findByCode(String code);

	List<Group> findAll();
}
