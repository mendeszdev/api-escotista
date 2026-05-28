package com.escoteiros.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtConfig {

    public static final String SECRET               = prop("JWT_SECRET",
        "escoteiros-secret-key-padrao-mude-em-producao-2024!");

    public static final int ACCESS_EXPIRY_MINUTES   = Integer.parseInt(
        prop("JWT_ACCESS_EXPIRY_MINUTES", "30"));

    public static final int REFRESH_EXPIRY_DAYS     = Integer.parseInt(
        prop("JWT_REFRESH_EXPIRY_DAYS", "7"));

    public static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private static String prop(String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp;
        return EnvLoader.INSTANCE.get(key, defaultValue);
    }

    private JwtConfig() {}
}
