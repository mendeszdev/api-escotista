package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.AcaoEducativa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AcaoEducativaDAO {

    private static final String COLS =
        "id, bloco_id, nome, descricao, tipo, e_especialidade, criado_por, ativo, criado_em, atualizado_em";

    public List<AcaoEducativa> listarTodos() throws SQLException {
        List<AcaoEducativa> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM acoes_educativas ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<AcaoEducativa> listarPorBloco(UUID blocoId) throws SQLException {
        List<AcaoEducativa> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM acoes_educativas WHERE bloco_id = ? ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, blocoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public AcaoEducativa buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM acoes_educativas WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public AcaoEducativa inserir(AcaoEducativa a) throws SQLException {
        String sql = """
            INSERT INTO acoes_educativas (bloco_id, nome, descricao, tipo, e_especialidade, criado_por, ativo)
            VALUES (?, ?, ?, ?::acao_tipo, ?, ?, ?)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, a.getBlocoId());
            st.setString(2, a.getNome());
            st.setString(3, a.getDescricao());
            st.setString(4, a.getTipo());
            st.setBoolean(5, a.isEEspecialidade());
            st.setObject(6, a.getCriadoPor());
            st.setBoolean(7, a.isAtivo());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                a.setId((UUID) rs.getObject("id"));
                a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        return a;
    }

    public boolean atualizar(AcaoEducativa a) throws SQLException {
        String sql = "UPDATE acoes_educativas SET nome = ?, descricao = ?, tipo = ?::acao_tipo, e_especialidade = ?, ativo = ? WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, a.getNome());
            st.setString(2, a.getDescricao());
            st.setString(3, a.getTipo());
            st.setBoolean(4, a.isEEspecialidade());
            st.setBoolean(5, a.isAtivo());
            st.setObject(6, a.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM acoes_educativas WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private AcaoEducativa mapear(ResultSet rs) throws SQLException {
        AcaoEducativa a = new AcaoEducativa();
        a.setId((UUID) rs.getObject("id"));
        a.setBlocoId((UUID) rs.getObject("bloco_id"));
        a.setNome(rs.getString("nome"));
        a.setDescricao(rs.getString("descricao"));
        a.setTipo(rs.getString("tipo"));
        a.setEEspecialidade(rs.getBoolean("e_especialidade"));
        a.setCriadoPor((UUID) rs.getObject("criado_por"));
        a.setAtivo(rs.getBoolean("ativo"));
        a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return a;
    }
}
