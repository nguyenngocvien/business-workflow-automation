package com.connector.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.ScheduleJobEntity;

public interface ScheduleJobRepository extends JpaRepository<ScheduleJobEntity, Long> {

    Optional<ScheduleJobEntity> findByJobCode(String jobCode);
}
