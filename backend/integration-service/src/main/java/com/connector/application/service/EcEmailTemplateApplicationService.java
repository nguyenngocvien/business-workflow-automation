package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.connector.application.common.AbstractCrudApplicationService;
import com.connector.application.dto.request.EcEmailTemplateRequest;
import com.connector.application.dto.response.EcEmailTemplateResponse;
import com.connector.domain.entity.EcEmailTemplate;
import com.connector.domain.repository.EcEmailTemplateRepository;

@Service
public class EcEmailTemplateApplicationService
    extends AbstractCrudApplicationService<EcEmailTemplateRequest, EcEmailTemplateResponse, EcEmailTemplate, Long> {

    public EcEmailTemplateApplicationService(EcEmailTemplateRepository repository) {
        super(repository, "EcEmailTemplate");
    }

    @Override
    protected EcEmailTemplate newEntity() {
        return new EcEmailTemplate();
    }

    @Override
    protected EcEmailTemplateResponse toResponse(EcEmailTemplate entity) {
        return new EcEmailTemplateResponse(
            entity.getId(),
            entity.getAppId(),
            entity.getTemplateType(),
            entity.getTemplateCode(),
            entity.getTitle(),
            entity.getContent(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.getUpdatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    @Override
    protected void updateEntity(EcEmailTemplate entity, EcEmailTemplateRequest request, boolean creating) {
        entity.setAppId(request.appId());
        entity.setTemplateType(request.templateType());
        entity.setTemplateCode(request.templateCode());
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setStatus(request.status() != null ? request.status() : Boolean.TRUE);
        entity.setCreatedBy(request.createdBy());
        entity.setUpdatedBy(request.updatedBy());
        entity.setCreatedAt(creating ? defaultNow(request.createdAt()) : entity.getCreatedAt());
        entity.setUpdatedAt(defaultNow(request.updatedAt()));
    }

    private LocalDateTime defaultNow(LocalDateTime value) {
        return value != null ? value : LocalDateTime.now();
    }
}
