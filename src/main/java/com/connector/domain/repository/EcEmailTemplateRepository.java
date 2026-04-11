package com.connector.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EcEmailTemplate;

public interface EcEmailTemplateRepository extends JpaRepository<EcEmailTemplate, Long> {

    Optional<EcEmailTemplate> findByAppIdAndTemplateTypeAndTemplateCode(
        String appId,
        String templateType,
        String templateCode
    );

    Optional<EcEmailTemplate> findByAppIdAndTemplateCode(
        String appId,
        String templateCode
    );

    List<EcEmailTemplate> findByAppId(String appId);
}
