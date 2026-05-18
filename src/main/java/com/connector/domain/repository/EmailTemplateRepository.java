package com.connector.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.domain.entity.EmailTemplateEntity;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, Long> {

    Optional<EmailTemplateEntity> findByAppIdAndTemplateTypeAndTemplateCode(
        String appId,
        String templateType,
        String templateCode
    );

    Optional<EmailTemplateEntity> findByAppIdAndTemplateCode(
        String appId,
        String templateCode
    );

    List<EmailTemplateEntity> findByAppId(String appId);
}
