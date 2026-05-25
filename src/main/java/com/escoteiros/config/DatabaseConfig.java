package com.escoteiros.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuração da conexão JDBC com o banco PostgreSQL do Supabase.
 *
 * As credenciais são lidas do arquivo .env na raiz do projeto.
 * O .env NUNCA deve ser commitado no Git (já está no .gitignore).
 *
 * Copie o .env.example para .env e preencha com suas credenciais:
 *   cp .env.example .env
 */
public class DatabaseConfig {

    private static final Dotenv dotenv = Dotenv.configure()
        .directory("./")       // procura o .env na raiz do projeto
        .ignoreIfMissing()     // não quebra a aplicação se .env não existir
        .load();

    private static final String HOST     = System.getProperty("DB_HOST",     dotenv.get("DB_HOST",     "localhost"));
    private static final String PORT     = System.getProperty("DB_PORT",     dotenv.get("DB_PORT",     "5432"));
    private static final String DATABASE = System.getProperty("DB_NAME",     dotenv.get("DB_NAME",     "postgres"));
    private static final String USER     = System.getProperty("DB_USER",     dotenv.get("DB_USER",     "postgres"));
    private static final String PASSWORD = System.getProperty("DB_PASS",     dotenv.get("DB_PASS",     ""));

    private static final String URL = String.format(
        "jdbc:postgresql://%s:%s/%s?sslmode=require", HOST, PORT, DATABASE
    );

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL não encontrado no classpath!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private DatabaseConfig() {}
}
