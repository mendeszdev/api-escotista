package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.GrupoEscoteiro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrupoEscotelroDAO {

    // ── LISTAR TODOS ────────────────────────────────────────────────────────
    public List<GrupoEscoteiro> listarTodos() throws SQLException {
        List<GrupoEscoteiro> lista = new ArrayList<>();
        String sql = """
            SELECT id, nome, numero, distrito, regiao,
                   paleta_cores::text, logo_url, status, criado_em, atualizado_em
            FROM grupos_escoteiros
            ORDER BY nome
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── BUSCAR POR ID ───────────────────────────────────────────────────────
    public GrupoEscoteiro buscarPorId(UUID id) throws SQLException {
        String sql = """
            SELECT id, nome, numero, distrito, regiao,
                   paleta_cores::text, logo_url, status, criado_em, atualizado_em
            FROM grupos_escoteiros WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    // ── INSERIR ─────────────────────────────────────────────────────────────
    public GrupoEscoteiro inserir(GrupoEscoteiro g) throws SQLException {
        String sql = """
            INSERT INTO grupos_escoteiros (nome, numero, distrito, regiao, logo_url, status)
            VALUES (?, ?, ?, ?, ?, ?::status_geral)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, g.getNome());
            st.setString(2, g.getNumero());
            st.setString(3, g.getDistrito());
            st.setString(4, g.getRegiao());
            st.setString(5, g.getLogoUrl());
            st.setString(6, g.getStatus() != null ? g.getStatus() : "ativo");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                g.setId((UUID) rs.getObject("id"));
                g.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                g.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        return g;
    }

    // ── ATUALIZAR ───────────────────────────────────────────────────────────
    public boolean atualizar(GrupoEscoteiro g) throws SQLException {
        String sql = """
            UPDATE grupos_escoteiros
            SET nome = ?, numero = ?, distrito = ?, regiao = ?,
                logo_url = ?, status = ?::status_geral
            WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, g.getNome());
            st.setString(2, g.getNumero());
            st.setString(3, g.getDistrito());
            st.setString(4, g.getRegiao());
            st.setString(5, g.getLogoUrl());
            st.setString(6, g.getStatus());
            st.setObject(7, g.getId());
            return st.executeUpdate() > 0;
        }
    }

    // ── DELETAR ─────────────────────────────────────────────────────────────
    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM grupos_escoteiros WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    // ── MAPEAMENTO ──────────────────────────────────────────────────────────
    private GrupoEscoteiro mapear(ResultSet rs) throws SQLException {
        GrupoEscoteiro g = new GrupoEscoteiro();
        g.setId((UUID) rs.getObject("id"));
        g.setNome(rs.getString("nome"));
        g.setNumero(rs.getString("numero"));
        g.setDistrito(rs.getString("distrito"));
        g.setRegiao(rs.getString("regiao"));
        g.setPaletaCores(rs.getString("paleta_cores"));
        g.setLogoUrl(rs.getString("logo_url"));
        g.setStatus(rs.getString("status"));
        g.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        g.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return g;
    }
}
