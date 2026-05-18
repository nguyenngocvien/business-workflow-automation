package com.connector.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findByServiceCode(String serviceCode);

    List<ServiceEntity> findByAppId(String appId);

    Optional<ServiceEntity> findByAppIdAndServiceCodeAndServiceVersion(
        String appId,
        String serviceCode,
        String serviceVersion
    );
}
