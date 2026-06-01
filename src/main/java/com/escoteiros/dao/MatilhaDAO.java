package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Matilha;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatilhaDAO {

    private static final String COLS = "id, alcateia_id, nome, cor, icone_url, status, criado_em, atualizado_em";

    public List<Matilha> listarTodos() throws SQLException {
        List<Matilha> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM matilhas ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Matilha> listarPorAlcateia(UUID alcateiaId) throws SQLException {
        List<Matilha> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM matilhas WHERE alcateia_id = ? ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, alcateiaId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Matilha buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM matilhas WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Matilha inserir(Matilha m) throws SQLException {
        String sql = """
            INSERT INTO matilhas (alcateia_id, nome, cor, icone_url, status)
            VALUES (?, ?, ?, ?, ?::status_geral)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, m.getAlcateiaId());
            st.setString(2, m.getNome());
            st.setString(3, m.getCor());
            st.setString(4, m.getIconeUrl());
            st.setString(5, m.getStatus() != null ? m.getStatus() : "ativo");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                m.setId((UUID) rs.getObject("id"));
                m.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                m.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        return m;
    }

    public boolean atualizar(Matilha m) throws SQLException {
        String sql = "UPDATE matilhas SET alcateia_id = ?, nome = ?, cor = ?, icone_url = ?, status = ?::status_geral WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, m.getAlcateiaId());
            st.setString(2, m.getNome());
            st.setString(3, m.getCor());
            st.setString(4, m.getIconeUrl());
            st.setString(5, m.getStatus());
            st.setObject(6, m.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM matilhas WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private Matilha mapear(ResultSet rs) throws SQLException {
        Matilha m = new Matilha();
        m.setId((UUID) rs.getObject("id"));
        m.setAlcateiaId((UUID) rs.getObject("alcateia_id"));
        m.setNome(rs.getString("nome"));
        m.setCor(rs.getString("cor"));
        m.setIconeUrl(rs.getString("icone_url"));
        m.setStatus(rs.getString("status"));
        m.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        m.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return m;
    }
}
