package com.connector.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcConnection;

public interface EcConnectionRepository extends JpaRepository<EcConnection, Long> {

    Optional<EcConnection> findByConnectionCode(String connectionCode);
}
