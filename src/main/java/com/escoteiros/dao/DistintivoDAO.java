package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Distintivo;

import java.sql.*;
import java.util.*;

public class DistintivoDAO {

    public List<Distintivo> listarTodos() throws SQLException {
        List<Distintivo> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, icone, cor_hex, e_especialidade, ativo, criado_em FROM distintivos ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Distintivo buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, nome, descricao, icone, cor_hex, e_especialidade, ativo, criado_em FROM distintivos WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Distintivo inserir(Distintivo d) throws SQLException {
        String sql = "INSERT INTO distintivos (nome, descricao, icone, cor_hex, e_especialidade, ativo) VALUES (?, ?, ?, ?, ?, ?) RETURNING id, criado_em";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, d.getNome()); st.setString(2, d.getDescricao());
            st.setString(3, d.getIcone()); st.setString(4, d.getCorHex());
            st.setBoolean(5, d.isEEspecialidade()); st.setBoolean(6, d.isAtivo());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                d.setId((UUID) rs.getObject("id"));
                d.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
            }
        }
        return d;
    }

    public boolean atualizar(Distintivo d) throws SQLException {
        String sql = "UPDATE distintivos SET nome=?, descricao=?, icone=?, cor_hex=?, e_especialidade=?, ativo=? WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, d.getNome()); st.setString(2, d.getDescricao());
            st.setString(3, d.getIcone()); st.setString(4, d.getCorHex());
            st.setBoolean(5, d.isEEspecialidade()); st.setBoolean(6, d.isAtivo());
            st.setObject(7, d.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement("DELETE FROM distintivos WHERE id=?")) {
            st.setObject(1, id); return st.executeUpdate() > 0;
        }
    }

    private Distintivo mapear(ResultSet rs) throws SQLException {
        Distintivo d = new Distintivo();
        d.setId((UUID) rs.getObject("id")); d.setNome(rs.getString("nome"));
        d.setDescricao(rs.getString("descricao")); d.setIcone(rs.getString("icone"));
        d.setCorHex(rs.getString("cor_hex")); d.setEEspecialidade(rs.getBoolean("e_especialidade"));
        d.setAtivo(rs.getBoolean("ativo"));
        d.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        return d;
    }
}
