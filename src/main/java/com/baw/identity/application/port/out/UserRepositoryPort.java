package com.baw.identity.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.baw.identity.domain.model.User;

public interface UserRepositoryPort {

	User save(User user);

	Optional<User> findById(Long id);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByExternalId(UUID externalId);

	List<User> findAll();
}
