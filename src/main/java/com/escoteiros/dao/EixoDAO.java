package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Eixo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EixoDAO {

    public List<Eixo> listarTodos() throws SQLException {
        List<Eixo> lista = new ArrayList<>();
        String sql = "SELECT id, tipo::text, nome, descricao, icone, cor_hex, ordem FROM eixos ORDER BY ordem";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Eixo buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, tipo::text, nome, descricao, icone, cor_hex, ordem FROM eixos WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Eixo buscarPorTipo(String tipo) throws SQLException {
        String sql = "SELECT id, tipo::text, nome, descricao, icone, cor_hex, ordem FROM eixos WHERE tipo = ?::eixo_tipo";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, tipo);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Eixo inserir(Eixo e) throws SQLException {
        String sql = """
            INSERT INTO eixos (tipo, nome, descricao, icone, cor_hex, ordem)
            VALUES (?::eixo_tipo, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, e.getTipo());
            st.setString(2, e.getNome());
            st.setString(3, e.getDescricao());
            st.setString(4, e.getIcone());
            st.setString(5, e.getCorHex());
            st.setInt(6, e.getOrdem());
            ResultSet rs = st.executeQuery();
            if (rs.next()) e.setId((UUID) rs.getObject("id"));
        }
        return e;
    }

    public boolean atualizar(Eixo e) throws SQLException {
        String sql = """
            UPDATE eixos SET nome = ?, descricao = ?, icone = ?, cor_hex = ?, ordem = ?
            WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, e.getNome());
            st.setString(2, e.getDescricao());
            st.setString(3, e.getIcone());
            st.setString(4, e.getCorHex());
            st.setInt(5, e.getOrdem());
            st.setObject(6, e.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM eixos WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private Eixo mapear(ResultSet rs) throws SQLException {
        Eixo e = new Eixo();
        e.setId((UUID) rs.getObject("id"));
        e.setTipo(rs.getString("tipo"));
        e.setNome(rs.getString("nome"));
        e.setDescricao(rs.getString("descricao"));
        e.setIcone(rs.getString("icone"));
        e.setCorHex(rs.getString("cor_hex"));
        e.setOrdem(rs.getInt("ordem"));
        return e;
    }
}
