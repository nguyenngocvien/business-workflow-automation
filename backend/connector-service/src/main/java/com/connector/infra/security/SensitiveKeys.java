package com.connector.infra.security;

import java.util.Set;

public class SensitiveKeys {

    public static final Set<String> KEYS = Set.of(
        "password",
        "secret",
        "token",
        "apiKey",
        "clientSecret",
        "accessKey"
    );

    public static boolean isSensitive(String key) {
        return KEYS.contains(key);
    }
}