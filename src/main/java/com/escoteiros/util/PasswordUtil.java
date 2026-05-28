package com.escoteiros.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int CUSTO = 12;

    public static String hash(String senhaRaw) {
        return BCrypt.hashpw(senhaRaw, BCrypt.gensalt(CUSTO));
    }

    public static boolean verificar(String senhaRaw, String hashSalvo) {
        if (hashSalvo == null || hashSalvo.isBlank()) return false;
        return BCrypt.checkpw(senhaRaw, hashSalvo);
    }

    private PasswordUtil() {}
}
