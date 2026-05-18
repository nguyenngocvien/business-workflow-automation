package com.connector.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.ConnectionEntity;

public interface ConnectionRepository extends JpaRepository<ConnectionEntity, Long> {

    Optional<ConnectionEntity> findByConnectionCode(String connectionCode);
}
