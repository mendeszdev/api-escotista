package com.escoteiros.util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Armazena tokens de sessão em memória com expiração automática.
 * Substituir por solução distribuída (Redis, JWT) em ambientes com múltiplas instâncias.
 */
public final class TokenStore {

    private static final Duration TTL = Duration.ofHours(8);

    // token → instante de expiração
    private static final ConcurrentHashMap<String, Instant> store = new ConcurrentHashMap<>();

    private TokenStore() {}

    /** Registra um token e retorna ele mesmo para uso em cadeia. */
    public static String register(String token) {
        purgeExpired();
        store.put(token, Instant.now().plus(TTL));
        return token;
    }

    /** Retorna true se o token existe e ainda não expirou. */
    public static boolean isValid(String token) {
        if (token == null) return false;
        Instant expiry = store.get(token);
        if (expiry == null) return false;
        if (Instant.now().isAfter(expiry)) {
            store.remove(token);
            return false;
        }
        return true;
    }

    /** Invalida o token imediatamente (logout). */
    public static void revoke(String token) {
        store.remove(token);
    }

    private static void purgeExpired() {
        Instant now = Instant.now();
        store.entrySet().removeIf(e -> now.isAfter(e.getValue()));
    }
}
