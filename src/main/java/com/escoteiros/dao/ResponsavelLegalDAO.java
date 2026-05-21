package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.ResponsavelLegal;

import java.sql.*;
import java.util.*;

public class ResponsavelLegalDAO {

    public List<ResponsavelLegal> listarPorAssociado(UUID associadoId) throws SQLException {
        List<ResponsavelLegal> lista = new ArrayList<>();
        String sql = "SELECT id, associado_id, nome_completo, parentesco, cpf, telefone, email, responsavel_financeiro, criado_em FROM responsaveis_legais WHERE associado_id = ? ORDER BY nome_completo";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public ResponsavelLegal buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, associado_id, nome_completo, parentesco, cpf, telefone, email, responsavel_financeiro, criado_em FROM responsaveis_legais WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public ResponsavelLegal inserir(ResponsavelLegal r) throws SQLException {
        String sql = "INSERT INTO responsaveis_legais (associado_id, nome_completo, parentesco, cpf, telefone, email, responsavel_financeiro) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, criado_em";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, r.getAssociadoId()); st.setString(2, r.getNomeCompleto());
            st.setString(3, r.getParentesco()); st.setString(4, r.getCpf());
            st.setString(5, r.getTelefone()); st.setString(6, r.getEmail());
            st.setBoolean(7, r.isResponsavelFinanceiro());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                r.setId((UUID) rs.getObject("id"));
                r.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
            }
        }
        return r;
    }

    public boolean atualizar(ResponsavelLegal r) throws SQLException {
        String sql = "UPDATE responsaveis_legais SET nome_completo=?, parentesco=?, cpf=?, telefone=?, email=?, responsavel_financeiro=? WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, r.getNomeCompleto()); st.setString(2, r.getParentesco());
            st.setString(3, r.getCpf()); st.setString(4, r.getTelefone());
            st.setString(5, r.getEmail()); st.setBoolean(6, r.isResponsavelFinanceiro());
            st.setObject(7, r.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement("DELETE FROM responsaveis_legais WHERE id=?")) {
            st.setObject(1, id); return st.executeUpdate() > 0;
        }
    }

    private ResponsavelLegal mapear(ResultSet rs) throws SQLException {
        ResponsavelLegal r = new ResponsavelLegal();
        r.setId((UUID) rs.getObject("id")); r.setAssociadoId((UUID) rs.getObject("associado_id"));
        r.setNomeCompleto(rs.getString("nome_completo")); r.setParentesco(rs.getString("parentesco"));
        r.setCpf(rs.getString("cpf")); r.setTelefone(rs.getString("telefone"));
        r.setEmail(rs.getString("email")); r.setResponsavelFinanceiro(rs.getBoolean("responsavel_financeiro"));
        r.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        return r;
    }
}
