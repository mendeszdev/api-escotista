package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.EscotistaAlcateia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EscotistaAlcateiaDAO {

    private static final String COLS =
        "id, associado_id, alcateia_id, data_inicio, data_fim, ativo";

    public List<EscotistaAlcateia> listarPorAlcateia(UUID alcateiaId) throws SQLException {
        List<EscotistaAlcateia> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM escotistas_alcateias WHERE alcateia_id = ? AND ativo = true";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, alcateiaId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<EscotistaAlcateia> listarPorAssociado(UUID associadoId) throws SQLException {
        List<EscotistaAlcateia> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM escotistas_alcateias WHERE associado_id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public EscotistaAlcateia buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM escotistas_alcateias WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public EscotistaAlcateia inserir(EscotistaAlcateia e) throws SQLException {
        String sql = """
            INSERT INTO escotistas_alcateias (associado_id, alcateia_id)
            VALUES (?, ?)
            RETURNING id, data_inicio
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, e.getAssociadoId());
            st.setObject(2, e.getAlcateiaId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                e.setId((UUID) rs.getObject("id"));
                Date di = rs.getDate("data_inicio");
                if (di != null) e.setDataInicio(di.toLocalDate());
            }
        }
        return e;
    }

    public boolean encerrar(UUID id) throws SQLException {
        String sql = "UPDATE escotistas_alcateias SET ativo = false, data_fim = CURRENT_DATE WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM escotistas_alcateias WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private EscotistaAlcateia mapear(ResultSet rs) throws SQLException {
        EscotistaAlcateia e = new EscotistaAlcateia();
        e.setId((UUID) rs.getObject("id"));
        e.setAssociadoId((UUID) rs.getObject("associado_id"));
        e.setAlcateiaId((UUID) rs.getObject("alcateia_id"));
        Date di = rs.getDate("data_inicio");
        if (di != null) e.setDataInicio(di.toLocalDate());
        Date df = rs.getDate("data_fim");
        if (df != null) e.setDataFim(df.toLocalDate());
        e.setAtivo(rs.getBoolean("ativo"));
        return e;
    }
}
