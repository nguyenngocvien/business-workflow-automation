package com.connector.application.port.out;

public interface EncryptionEngine {

    /**
     * Encrypt sensitive fields inside JSON string
     */
    String encrypt(String json);

    /**
     * Decrypt sensitive fields inside JSON string
     */
    String decrypt(String json);
}