package com.connector.api.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.command.CreateEmailTemplateCommand;
import com.connector.application.command.UpdateEmailTemplateCommand;
import com.connector.application.dto.CreateEmailTemplateRequest;
import com.connector.application.dto.UpdateEmailTemplateRequest;
import com.connector.application.result.EmailTemplateResult;
import com.connector.application.usecase.EmailTemplateDefinitionUseCase;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/email-templates")
@Tag(name = "Email Templates", description = "Manage email template definitions")
public class EmailTemplateDefinitionController {

    private final EmailTemplateDefinitionUseCase useCase;

    public EmailTemplateDefinitionController(EmailTemplateDefinitionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List email templates")
    public Page<EmailTemplateResult> findAll(@ParameterObject Pageable pageable) {
        return useCase.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an email template by id")
    public EmailTemplateResult findById(@Parameter(description = "Email template id") @PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create an email template")
    public ResponseEntity<EmailTemplateResult> create(@Valid @RequestBody CreateEmailTemplateRequest request) {
        CreateEmailTemplateCommand cmd = new CreateEmailTemplateCommand(
            request.appId(),
            request.templateType(),
            request.templateCode(),
            request.title(),
            request.content(),
            request.status(),
            request.createdBy()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(cmd));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an email template")
    public EmailTemplateResult update(
        @Parameter(description = "Email template id") @PathVariable Long id,
        @Valid @RequestBody UpdateEmailTemplateRequest request
    ) {
        UpdateEmailTemplateCommand cmd = new UpdateEmailTemplateCommand(
            id,
            request.templateType(),
            request.title(),
            request.content(),
            request.status(),
            request.updatedBy()
        );
        return useCase.update(cmd);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an email template")
    public ResponseEntity<Void> delete(@Parameter(description = "Email template id") @PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
