package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Documento;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DocumentoDAO {

    public List<Documento> listarPorAssociado(UUID associadoId) throws SQLException {
        List<Documento> lista = new ArrayList<>();
        String sql = "SELECT id, associado_id, tipo, nome_arquivo, url, tamanho_bytes, validade, obrigatorio, status, criado_em FROM documentos WHERE associado_id = ? ORDER BY criado_em DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Documento buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, associado_id, tipo, nome_arquivo, url, tamanho_bytes, validade, obrigatorio, status, criado_em FROM documentos WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Documento inserir(Documento d) throws SQLException {
        String sql = "INSERT INTO documentos (associado_id, tipo, nome_arquivo, url, tamanho_bytes, validade, obrigatorio, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?::status_geral) RETURNING id, criado_em";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, d.getAssociadoId()); st.setString(2, d.getTipo());
            st.setString(3, d.getNomeArquivo()); st.setString(4, d.getUrl());
            st.setObject(5, d.getTamanhoBytes()); st.setObject(6, d.getValidade());
            st.setBoolean(7, d.isObrigatorio());
            st.setString(8, d.getStatus() != null ? d.getStatus() : "ativo");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                d.setId((UUID) rs.getObject("id"));
                d.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
            }
        }
        return d;
    }

    public boolean deletar(UUID id) throws SQLException {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement("DELETE FROM documentos WHERE id=?")) {
            st.setObject(1, id); return st.executeUpdate() > 0;
        }
    }

    private Documento mapear(ResultSet rs) throws SQLException {
        Documento d = new Documento();
        d.setId((UUID) rs.getObject("id")); d.setAssociadoId((UUID) rs.getObject("associado_id"));
        d.setTipo(rs.getString("tipo")); d.setNomeArquivo(rs.getString("nome_arquivo"));
        d.setUrl(rs.getString("url")); d.setTamanhoBytes(rs.getLong("tamanho_bytes"));
        Date val = rs.getDate("validade");
        if (val != null) d.setValidade(val.toLocalDate());
        d.setObrigatorio(rs.getBoolean("obrigatorio")); d.setStatus(rs.getString("status"));
        d.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        return d;
    }
}
