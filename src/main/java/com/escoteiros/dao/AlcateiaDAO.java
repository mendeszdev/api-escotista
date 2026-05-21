package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Alcateia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlcateiaDAO {

    public List<Alcateia> listarTodos() throws SQLException {
        List<Alcateia> lista = new ArrayList<>();
        String sql = "SELECT id, grupo_escoteiro_id, nome, descricao, status, criado_em, atualizado_em FROM alcateias ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Alcateia> listarPorGrupo(UUID grupoId) throws SQLException {
        List<Alcateia> lista = new ArrayList<>();
        String sql = "SELECT id, grupo_escoteiro_id, nome, descricao, status, criado_em, atualizado_em FROM alcateias WHERE grupo_escoteiro_id = ? ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, grupoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Alcateia buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, grupo_escoteiro_id, nome, descricao, status, criado_em, atualizado_em FROM alcateias WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Alcateia inserir(Alcateia a) throws SQLException {
        String sql = """
            INSERT INTO alcateias (grupo_escoteiro_id, nome, descricao, status)
            VALUES (?, ?, ?, ?::status_geral)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, a.getGrupoEscotelroId());
            st.setString(2, a.getNome());
            st.setString(3, a.getDescricao());
            st.setString(4, a.getStatus() != null ? a.getStatus() : "ativo");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                a.setId((UUID) rs.getObject("id"));
                a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        return a;
    }

    public boolean atualizar(Alcateia a) throws SQLException {
        String sql = "UPDATE alcateias SET nome = ?, descricao = ?, status = ?::status_geral WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, a.getNome());
            st.setString(2, a.getDescricao());
            st.setString(3, a.getStatus());
            st.setObject(4, a.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM alcateias WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private Alcateia mapear(ResultSet rs) throws SQLException {
        Alcateia a = new Alcateia();
        a.setId((UUID) rs.getObject("id"));
        a.setGrupoEscotelroId((UUID) rs.getObject("grupo_escoteiro_id"));
        a.setNome(rs.getString("nome"));
        a.setDescricao(rs.getString("descricao"));
        a.setStatus(rs.getString("status"));
        a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return a;
    }
}
