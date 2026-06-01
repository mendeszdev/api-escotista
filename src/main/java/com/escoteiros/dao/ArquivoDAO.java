package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Arquivo;

import java.sql.*;
import java.util.UUID;

public class ArquivoDAO {

    /**
     * Salva o arquivo no banco e retorna o UUID gerado.
     */
    public UUID inserir(String nome, String tipoMime, byte[] dados) throws SQLException {
        String sql = "INSERT INTO arquivos (nome, tipo_mime, dados, tamanho_bytes) " +
                     "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, nome);
            st.setString(2, tipoMime);
            st.setBytes(3, dados);
            st.setLong(4, dados.length);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return (UUID) rs.getObject("id");
        }
        throw new SQLException("Falha ao inserir arquivo — nenhum ID retornado");
    }

    /**
     * Recupera o arquivo completo (incluindo bytes) pelo ID.
     */
    public Arquivo buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, nome, tipo_mime, dados, tamanho_bytes, criado_em " +
                     "FROM arquivos WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Arquivo a = new Arquivo();
                a.setId((UUID) rs.getObject("id"));
                a.setNome(rs.getString("nome"));
                a.setTipoMime(rs.getString("tipo_mime"));
                a.setDados(rs.getBytes("dados"));
                a.setTamanhoBytes(rs.getLong("tamanho_bytes"));
                a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                return a;
            }
        }
        return null;
    }

    public boolean deletar(UUID id) throws SQLException {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement("DELETE FROM arquivos WHERE id = ?")) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }
}
