package com.escoteiros.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtConfig {

    private static final Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMissing()
        .load();

    public static final String SECRET = System.getProperty("JWT_SECRET",
        dotenv.get("JWT_SECRET", "escoteiros-secret-key-padrao-mude-em-producao-2024!"));

    public static final int ACCESS_EXPIRY_MINUTES = Integer.parseInt(
        System.getProperty("JWT_ACCESS_EXPIRY_MINUTES",
            dotenv.get("JWT_ACCESS_EXPIRY_MINUTES", "30")));

    public static final int REFRESH_EXPIRY_DAYS = Integer.parseInt(
        System.getProperty("JWT_REFRESH_EXPIRY_DAYS",
            dotenv.get("JWT_REFRESH_EXPIRY_DAYS", "7")));

    public static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private JwtConfig() {}
}
