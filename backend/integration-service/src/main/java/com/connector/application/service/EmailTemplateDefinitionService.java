package com.connector.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.connector.application.command.CreateEmailTemplateCommand;
import com.connector.application.command.UpdateEmailTemplateCommand;
import com.connector.application.result.EmailTemplateResult;
import com.connector.domain.entity.EmailTemplateEntity;
import com.connector.domain.repository.EmailTemplateRepository;
import com.connector.application.usecase.EmailTemplateDefinitionUseCase;

@Service
public class EmailTemplateDefinitionService implements EmailTemplateDefinitionUseCase {

    private final EmailTemplateRepository repository;

    public EmailTemplateDefinitionService(EmailTemplateRepository repository) {
        this.repository = repository;
    }

    public Page<EmailTemplateResult> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public EmailTemplateResult findById(Long id) {
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Email template not found"));
    }

    @Override
    public EmailTemplateResult create(CreateEmailTemplateCommand command) {
        EmailTemplateEntity entity = new EmailTemplateEntity();
        entity.setAppId(command.appId());
        entity.setTemplateType(command.templateType());
        entity.setTemplateCode(command.templateCode());
        entity.setTitle(command.title());
        entity.setContent(command.content());
        entity.setStatus(command.status() != null ? command.status() : Boolean.TRUE);
        entity.setCreatedBy(command.createdBy());
        entity.setCreatedAt(LocalDateTime.now());

        return toResponse(repository.save(entity));
    }

    @Override
    public EmailTemplateResult update(UpdateEmailTemplateCommand command) {
        EmailTemplateEntity entity = repository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Email template not found"));

        entity.setTemplateType(command.templateType());
        entity.setTitle(command.title());
        entity.setContent(command.content());
        entity.setStatus(command.status() != null ? command.status() : Boolean.TRUE);
        entity.setUpdatedBy(command.updatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        return toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EmailTemplateResult toResponse(EmailTemplateEntity entity) {
        return new EmailTemplateResult(
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
}
