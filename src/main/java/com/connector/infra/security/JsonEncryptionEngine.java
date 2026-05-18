package com.connector.infra.security;

import org.springframework.stereotype.Component;

import com.connector.application.port.out.EncryptionEngine;

@Component
public class JsonEncryptionEngine implements EncryptionEngine {

    private final ConfigEncryptionEngine engine;

    public JsonEncryptionEngine(ConfigEncryptionEngine engine) {
        this.engine = engine;
    }

    @Override
    public String encrypt(String json) {
        return engine.encryptJson(json);
    }

    @Override
    public String decrypt(String json) {
        return engine.decryptJson(json);
    }
}