package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.MembroMatilha;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MembroMatilhaDAO {

    private static final String COLS =
        "id, matilha_id, associado_id, cargo::text, data_entrada, data_saida, ativo";

    public List<MembroMatilha> listarPorMatilha(UUID matilhaId) throws SQLException {
        List<MembroMatilha> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM membros_matilha WHERE matilha_id = ? AND ativo = true";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, matilhaId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public MembroMatilha buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM membros_matilha WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public MembroMatilha inserir(MembroMatilha m) throws SQLException {
        String sql = """
            INSERT INTO membros_matilha (matilha_id, associado_id, cargo)
            VALUES (?, ?, ?::cargo_matilha)
            RETURNING id, data_entrada
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, m.getMatilhaId());
            st.setObject(2, m.getAssociadoId());
            st.setString(3, m.getCargo() != null ? m.getCargo() : "membro");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                m.setId((UUID) rs.getObject("id"));
                Date de = rs.getDate("data_entrada");
                if (de != null) m.setDataEntrada(de.toLocalDate());
            }
        }
        return m;
    }

    public boolean encerrar(UUID id) throws SQLException {
        String sql = "UPDATE membros_matilha SET ativo = false, data_saida = CURRENT_DATE WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM membros_matilha WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private MembroMatilha mapear(ResultSet rs) throws SQLException {
        MembroMatilha m = new MembroMatilha();
        m.setId((UUID) rs.getObject("id"));
        m.setMatilhaId((UUID) rs.getObject("matilha_id"));
        m.setAssociadoId((UUID) rs.getObject("associado_id"));
        m.setCargo(rs.getString("cargo"));
        Date de = rs.getDate("data_entrada");
        if (de != null) m.setDataEntrada(de.toLocalDate());
        Date ds = rs.getDate("data_saida");
        if (ds != null) m.setDataSaida(ds.toLocalDate());
        m.setAtivo(rs.getBoolean("ativo"));
        return m;
    }
}
