package com.connector.infra.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

public class ConfigEncryptionEngine {

    private final ObjectMapper objectMapper;
    private final CryptoService crypto;

    public ConfigEncryptionEngine(ObjectMapper objectMapper, CryptoService crypto) {
        this.objectMapper = objectMapper;
        this.crypto = crypto;
    }

    // ===============================
    // ENCRYPT
    // ===============================
    public String encryptJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            JsonNode encrypted = processEncrypt(node);
            return objectMapper.writeValueAsString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encrypt JSON failed", e);
        }
    }

    private JsonNode processEncrypt(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;

            obj.properties().forEach(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (value.isTextual() && SensitiveKeys.isSensitive(key)) {
                    if (!crypto.isEncrypted(value.asText())) {
                        obj.put(key, crypto.encrypt(value.asText()));
                    }
                } else {
                    obj.set(key, processEncrypt(value));
                }
            });

            return obj;
        }

        if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, processEncrypt(arr.get(i)));
            }
            return arr;
        }

        return node;
    }

    // ===============================
    // DECRYPT
    // ===============================
    public String decryptJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            JsonNode decrypted = processDecrypt(node);
            return objectMapper.writeValueAsString(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decrypt JSON failed", e);
        }
    }

    private JsonNode processDecrypt(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;

            obj.properties().forEach(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (value.isTextual() && crypto.isEncrypted(value.asText())) {
                    obj.put(key, crypto.decrypt(value.asText()));
                } else {
                    obj.set(key, processDecrypt(value));
                }
            });

            return obj;
        }

        if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, processDecrypt(arr.get(i)));
            }
            return arr;
        }

        return node;
    }
}