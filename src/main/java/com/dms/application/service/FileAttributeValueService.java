package com.dms.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dms.application.command.UpsertFileAttributesCommand;
import com.dms.domain.entity.FileAttribute;
import com.dms.domain.entity.FileAttributeOption;
import com.dms.domain.entity.FileAttributeValue;
import com.dms.domain.entity.FileEntity;
import com.dms.domain.repository.FileAttributeOptionRepository;
import com.dms.domain.repository.FileAttributeRepository;
import com.dms.domain.repository.FileAttributeValueRepository;
import com.dms.domain.repository.FileRepository;
import com.dms.interfaces.rest.request.FileAttributeValueRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileAttributeValueService {

    private final FileRepository fileRepository;
    private final FileAttributeRepository attributeRepository;
    private final FileAttributeOptionRepository optionRepository;
    private final FileAttributeValueRepository valueRepository;

    public void upsert(UpsertFileAttributesCommand cmd) {

        FileEntity file = fileRepository.findById(cmd.getFileId())
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // load all attributes
        Map<String, FileAttribute> attrMap = attributeRepository.findAll().stream()
                .collect(Collectors.toMap(FileAttribute::getKeyCode, a -> a));

        // existing values
        Map<Long, FileAttributeValue> existing = valueRepository.findByFileId(file.getId())
                .stream()
                .collect(Collectors.toMap(v -> v.getAttribute().getId(), v -> v));

        for (FileAttributeValueRequest req : cmd.getAttributes()) {

            FileAttribute attr = attrMap.get(req.getKey());
            if (attr == null) {
                throw new IllegalArgumentException("Unknown attribute: " + req.getKey());
            }

            FileAttributeValue val = existing.getOrDefault(attr.getId(), new FileAttributeValue());

            val.setFile(file);
            val.setAttribute(attr);
            val.setCreatedAt(Instant.now());

            applyValue(val, attr, req.getValue());

            if (val.getId() == null) {
                valueRepository.save(val);
            }
        }
    }

    private void applyValue(FileAttributeValue val, FileAttribute attr, Object raw) {

        // clear all
        val.setValueString(null);
        val.setValueNumber(null);
        val.setValueBoolean(null);
        val.setValueDate(null);
        val.setOption(null);

        switch (attr.getDataType()) {

            case STRING:
                val.setValueString(raw.toString());
                break;

            case NUMBER:
                val.setValueNumber(toBigDecimal(raw));
                break;

            case BOOLEAN:
                val.setValueBoolean(Boolean.valueOf(raw.toString()));
                break;

            case DATE:
                val.setValueDate((Instant) raw);
                break;

            case LIST:
                FileAttributeOption option = optionRepository
                        .findByAttributeIdAndOptionValue(attr.getId(), raw.toString())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid option"));
                val.setOption(option);
                break;

            default:
                throw new IllegalStateException("Unsupported type");
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        throw new IllegalArgumentException("Invalid number");
    }
}