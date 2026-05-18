package com.connector.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.LogEntity;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
}
