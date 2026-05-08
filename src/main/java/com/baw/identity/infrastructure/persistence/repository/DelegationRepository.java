package com.baw.identity.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baw.identity.infrastructure.persistence.entity.DelegationEntity;

public interface DelegationRepository extends JpaRepository<DelegationEntity, Long> {
}
