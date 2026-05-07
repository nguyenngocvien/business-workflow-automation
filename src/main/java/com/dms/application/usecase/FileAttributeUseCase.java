package com.dms.application.usecase;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dms.application.command.AddOptionCommand;
import com.dms.application.command.CreateFileAttributeCommand;
import com.dms.application.result.FileAttributeResult;
import com.dms.domain.entity.FileAttribute;
import com.dms.domain.entity.FileAttributeOption;
import com.dms.domain.enums.AttributeDataType;
import com.dms.domain.repository.FileAttributeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileAttributeUseCase {

    private final FileAttributeRepository repository;

    @Transactional
    public FileAttributeResult create(CreateFileAttributeCommand cmd) {

        repository.findByKeyCode(cmd.getKeyCode())
                .ifPresent(a -> {
                    throw new IllegalArgumentException("Attribute already exists");
                });

        FileAttribute entity = new FileAttribute();
        entity.setKeyCode(cmd.getKeyCode());
        entity.setDisplayName(cmd.getDisplayName());
        entity.setDataType(cmd.getDataType());
        entity.setIsRequired(cmd.getIsRequired());
        entity.setCreatedAt(Instant.now());

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<FileAttributeResult> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void addOption(AddOptionCommand cmd) {

        FileAttribute attribute = repository.findById(cmd.getAttributeId())
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found"));

        // chỉ LIST mới có option
        if (attribute.getDataType() != AttributeDataType.LIST) {
            throw new IllegalArgumentException("Only LIST type can have options");
        }

        boolean exists = attribute.getOptions().stream()
                .anyMatch(o -> o.getOptionValue().equals(cmd.getOptionValue()));

        if (exists) {
            throw new IllegalArgumentException("Option already exists");
        }

        FileAttributeOption option = new FileAttributeOption();
        option.setOptionLabel(cmd.getOptionLabel());
        option.setOptionValue(cmd.getOptionValue());
        option.setSortOrder(cmd.getSortOrder());
        option.setAttribute(attribute);

        attribute.getOptions().add(option);
    }

    private FileAttributeResult toResponse(FileAttribute e) {
        return new FileAttributeResult(
                e.getId(),
                e.getKeyCode(),
                e.getDisplayName(),
                e.getDataType(),
                e.getIsRequired()
        );
    }
}