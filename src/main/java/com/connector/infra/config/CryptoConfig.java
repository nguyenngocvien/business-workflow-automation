package com.connector.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.connector.infra.security.ConfigEncryptionEngine;
import com.connector.infra.security.CryptoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class CryptoConfig {

    @Value("${app.crypto.key}")
    private String key;

    @Bean
    public CryptoService cryptoService() {
        return new CryptoService(key);
    }

    @Bean
    public ConfigEncryptionEngine encryptionEngine(
        ObjectMapper objectMapper,
        CryptoService cryptoService
    ) {
        return new ConfigEncryptionEngine(objectMapper, cryptoService);
    }
}