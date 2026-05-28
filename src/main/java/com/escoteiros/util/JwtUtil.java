package com.escoteiros.util;

import com.escoteiros.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    public static String gerarAccessToken(UUID associadoId, String matricula,
                                          String role, String nome, UUID grupoId) {
        long now    = System.currentTimeMillis();
        long expiry = now + (long) JwtConfig.ACCESS_EXPIRY_MINUTES * 60 * 1000;

        return Jwts.builder()
            .subject(associadoId.toString())
            .claim("type",      "access")
            .claim("matricula", matricula)
            .claim("role",      role)
            .claim("name",      nome)
            .claim("grupoId",   grupoId != null ? grupoId.toString() : null)
            .issuedAt(new Date(now))
            .expiration(new Date(expiry))
            .signWith(JwtConfig.getSecretKey())
            .compact();
    }

    public static String gerarRefreshToken(UUID associadoId) {
        long now    = System.currentTimeMillis();
        long expiry = now + (long) JwtConfig.REFRESH_EXPIRY_DAYS * 24 * 60 * 60 * 1000;

        return Jwts.builder()
            .subject(associadoId.toString())
            .claim("type", "refresh")
            .issuedAt(new Date(now))
            .expiration(new Date(expiry))
            .signWith(JwtConfig.getSecretKey())
            .compact();
    }

    public static Claims validar(String token) {
        return Jwts.parser()
            .verifyWith(JwtConfig.getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public static boolean isValido(String token) {
        try {
            validar(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private JwtUtil() {}
}
