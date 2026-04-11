package com.connector.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcScheduleJob;

public interface EcScheduleJobRepository extends JpaRepository<EcScheduleJob, Long> {

    Optional<EcScheduleJob> findByJobCode(String jobCode);
}
