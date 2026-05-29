package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.BlocoAprendizagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlocoAprendizagemDAO {

    private static final String COLS =
        "id, eixo_id, nome, descricao, min_acoes_variaveis, ordem, ativo";

    public List<BlocoAprendizagem> listarTodos() throws SQLException {
        List<BlocoAprendizagem> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM blocos_aprendizagem ORDER BY ordem";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<BlocoAprendizagem> listarPorEixo(UUID eixoId) throws SQLException {
        List<BlocoAprendizagem> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM blocos_aprendizagem WHERE eixo_id = ? ORDER BY ordem";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, eixoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public BlocoAprendizagem buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM blocos_aprendizagem WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public BlocoAprendizagem inserir(BlocoAprendizagem b) throws SQLException {
        String sql = """
            INSERT INTO blocos_aprendizagem (eixo_id, nome, descricao, min_acoes_variaveis, ordem, ativo)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, b.getEixoId());
            st.setString(2, b.getNome());
            st.setString(3, b.getDescricao());
            st.setInt(4, b.getMinAcoesVariaveis());
            st.setInt(5, b.getOrdem());
            st.setBoolean(6, b.isAtivo());
            ResultSet rs = st.executeQuery();
            if (rs.next()) b.setId((UUID) rs.getObject("id"));
        }
        return b;
    }

    public boolean atualizar(BlocoAprendizagem b) throws SQLException {
        String sql = """
            UPDATE blocos_aprendizagem
            SET nome = ?, descricao = ?, min_acoes_variaveis = ?, ordem = ?, ativo = ?
            WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, b.getNome());
            st.setString(2, b.getDescricao());
            st.setInt(3, b.getMinAcoesVariaveis());
            st.setInt(4, b.getOrdem());
            st.setBoolean(5, b.isAtivo());
            st.setObject(6, b.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM blocos_aprendizagem WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private BlocoAprendizagem mapear(ResultSet rs) throws SQLException {
        BlocoAprendizagem b = new BlocoAprendizagem();
        b.setId((UUID) rs.getObject("id"));
        b.setEixoId((UUID) rs.getObject("eixo_id"));
        b.setNome(rs.getString("nome"));
        b.setDescricao(rs.getString("descricao"));
        b.setMinAcoesVariaveis(rs.getInt("min_acoes_variaveis"));
        b.setOrdem(rs.getInt("ordem"));
        b.setAtivo(rs.getBoolean("ativo"));
        return b;
    }
}
