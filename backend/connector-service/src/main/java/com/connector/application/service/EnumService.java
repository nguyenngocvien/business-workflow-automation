package com.connector.application.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.connector.application.dto.response.EnumOptionResponse;
import com.connector.application.usecase.EnumUseCase;
import com.connector.domain.enums.ConnectionType;
import com.connector.domain.enums.DbOperationType;
import com.connector.domain.enums.HttpMethodType;
import com.connector.domain.enums.JobType;
import com.connector.domain.enums.KafkaSecurityProtocolType;
import com.connector.domain.enums.MailProtocolType;
import com.connector.domain.enums.ServiceType;

@Service
public class EnumService implements EnumUseCase {

    private static final List<String> ENUM_NAMES = List.of(
        "ConnectionType",
        "DbOperationType",
        "HttpMethodType",
        "JobType",
        "KafkaSecurityProtocolType",
        "MailProtocolType",
        "ServiceType"
    );

    @Override
    public Map<String, List<EnumOptionResponse>> findAll() {
        Map<String, List<EnumOptionResponse>> values = new LinkedHashMap<>();
        values.put("ConnectionType", options(ConnectionType.values()));
        values.put("DbOperationType", options(DbOperationType.values()));
        values.put("HttpMethodType", options(HttpMethodType.values()));
        values.put("JobType", options(JobType.values()));
        values.put("KafkaSecurityProtocolType", options(KafkaSecurityProtocolType.values()));
        values.put("MailProtocolType", options(MailProtocolType.values()));
        values.put("ServiceType", options(ServiceType.values()));
        return values;
    }

    @Override
    public List<EnumOptionResponse> findByName(String enumName) {
        return switch (enumName) {
            case "ConnectionType" -> options(ConnectionType.values());
            case "DbOperationType" -> options(DbOperationType.values());
            case "HttpMethodType" -> options(HttpMethodType.values());
            case "JobType" -> options(JobType.values());
            case "KafkaSecurityProtocolType" -> options(KafkaSecurityProtocolType.values());
            case "MailProtocolType" -> options(MailProtocolType.values());
            case "ServiceType" -> options(ServiceType.values());
            default -> throw new IllegalArgumentException("Unsupported enum: " + enumName + ". Supported: " + ENUM_NAMES);
        };
    }

    private List<EnumOptionResponse> options(Enum<?>[] values) {
        return Arrays.stream(values)
            .map(EnumOptionResponse::of)
            .toList();
    }
}
