package com.escoteiros.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.net.URL;

/**
 * Carrega o arquivo .env priorizando o classpath (src/main/resources/.env),
 * que funciona dentro do WAR independente do diretório de trabalho do Tomcat.
 * Fallback para o diretório de trabalho atual (útil em testes unitários).
 */
public class EnvLoader {

    public static final Dotenv INSTANCE = load();

    private static Dotenv load() {
        URL url = EnvLoader.class.getClassLoader().getResource(".env");
        if (url != null) {
            try {
                String dir = new File(url.toURI()).getParent();
                return Dotenv.configure().directory(dir).ignoreIfMissing().load();
            } catch (Exception ignored) {}
        }
        return Dotenv.configure().directory("./").ignoreIfMissing().load();
    }

    private EnvLoader() {}
}
