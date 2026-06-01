package com.escoteiros.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String HOST     = prop("DB_HOST", "localhost");
    private static final String PORT     = prop("DB_PORT", "5432");
    private static final String DATABASE = prop("DB_NAME", "postgres");
    private static final String USER     = prop("DB_USER", "postgres");
    private static final String PASSWORD = prop("DB_PASS", "");

    private static final String SSL  = prop("DB_SSL", "require");

    private static final String URL = String.format(
        "jdbc:postgresql://%s:%s/%s?sslmode=%s", HOST, PORT, DATABASE, SSL
    );

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL não encontrado no classpath!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String prop(String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp;
        return EnvLoader.INSTANCE.get(key, defaultValue);
    }

    private DatabaseConfig() {}
}
