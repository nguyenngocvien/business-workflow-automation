package com.connector.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcService;

public interface EcServiceRepository extends JpaRepository<EcService, Long> {

    Optional<EcService> findByServiceCode(String serviceCode);

    List<EcService> findByAppId(String appId);

    Optional<EcService> findByAppIdAndServiceCodeAndServiceVersion(
        String appId,
        String serviceCode,
        String serviceVersion
    );
}
