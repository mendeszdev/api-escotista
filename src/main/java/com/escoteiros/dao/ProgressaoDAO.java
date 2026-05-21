package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Progressao;

import java.sql.*;
import java.util.*;

public class ProgressaoDAO {

    public List<Progressao> listarPorAssociado(UUID associadoId) throws SQLException {
        List<Progressao> lista = new ArrayList<>();
        String sql = "SELECT id, associado_id, eixo_id, percentual_concluido, atualizado_em FROM progressoes WHERE associado_id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Progressao p = new Progressao();
                p.setId((UUID) rs.getObject("id"));
                p.setAssociadoId((UUID) rs.getObject("associado_id"));
                p.setEixoId((UUID) rs.getObject("eixo_id"));
                p.setPercentualConcluido(rs.getBigDecimal("percentual_concluido"));
                p.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Progressao> listarTodos() throws SQLException {
        List<Progressao> lista = new ArrayList<>();
        String sql = "SELECT id, associado_id, eixo_id, percentual_concluido, atualizado_em FROM progressoes";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Progressao p = new Progressao();
                p.setId((UUID) rs.getObject("id"));
                p.setAssociadoId((UUID) rs.getObject("associado_id"));
                p.setEixoId((UUID) rs.getObject("eixo_id"));
                p.setPercentualConcluido(rs.getBigDecimal("percentual_concluido"));
                p.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
                lista.add(p);
            }
        }
        return lista;
    }
}
