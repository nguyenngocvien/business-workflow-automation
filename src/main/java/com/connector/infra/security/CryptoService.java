package com.connector.infra.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoService {

    private final SecretKeySpec key;

    public CryptoService(String secretKey) {
        this.key = new SecretKeySpec(secretKey.getBytes(), "AES");
    }

    public String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return "ENC(" + Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes())) + ")";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String value) {
        try {
            if (!value.startsWith("ENC(")) return value;

            String raw = value.substring(4, value.length() - 1);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(Base64.getDecoder().decode(raw)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith("ENC(");
    }
}