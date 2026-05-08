package com.connector.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreateEmailTemplateCommand;
import com.connector.application.command.UpdateEmailTemplateCommand;
import com.connector.application.result.EmailTemplateResult;

public interface EmailTemplateDefinitionUseCase {

    Page<EmailTemplateResult> findAll(Pageable pageable);

    EmailTemplateResult findById(Long id);

    EmailTemplateResult create(CreateEmailTemplateCommand command);

    EmailTemplateResult update(UpdateEmailTemplateCommand command);

    void delete(Long id);
}
