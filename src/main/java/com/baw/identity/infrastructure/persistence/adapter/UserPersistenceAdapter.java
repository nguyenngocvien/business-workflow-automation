package com.baw.identity.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.baw.identity.application.port.out.UserRepositoryPort;
import com.baw.identity.domain.model.User;
import com.baw.identity.infrastructure.persistence.entity.UserEntity;
import com.baw.identity.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.baw.identity.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

	private final UserRepository userRepository;

	public UserPersistenceAdapter(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User save(User user) {
		UserEntity entity = UserPersistenceMapper.toEntity(user);
		return UserPersistenceMapper.toDomain(userRepository.save(entity));
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id).map(UserPersistenceMapper::toDomain);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username).map(UserPersistenceMapper::toDomain);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email).map(UserPersistenceMapper::toDomain);
	}

	@Override
	public Optional<User> findByExternalId(UUID externalId) {
		return userRepository.findByExternalId(externalId).map(UserPersistenceMapper::toDomain);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll().stream().map(UserPersistenceMapper::toDomain).toList();
	}
}
