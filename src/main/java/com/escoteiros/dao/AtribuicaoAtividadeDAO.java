package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.AtribuicaoAtividade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AtribuicaoAtividadeDAO {

    private static final String COLS = """
        id, atividade_id, associado_id, status, registrado_em,
        descricao_registro, midia_url, validado_por, validado_em, feedback,
        data_realizacao
        """;

    public List<AtribuicaoAtividade> listarTodos() throws SQLException {
        List<AtribuicaoAtividade> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM atribuicoes_atividade ORDER BY registrado_em DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<AtribuicaoAtividade> listarPorAssociado(UUID associadoId) throws SQLException {
        List<AtribuicaoAtividade> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM atribuicoes_atividade WHERE associado_id = ? ORDER BY registrado_em DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<AtribuicaoAtividade> listarPorAtividade(UUID atividadeId) throws SQLException {
        List<AtribuicaoAtividade> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM atribuicoes_atividade WHERE atividade_id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, atividadeId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public AtribuicaoAtividade buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM atribuicoes_atividade WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public AtribuicaoAtividade inserir(AtribuicaoAtividade a) throws SQLException {
        String sql = """
            INSERT INTO atribuicoes_atividade
              (atividade_id, associado_id, status, descricao_registro, midia_url, data_realizacao)
            VALUES (?, ?, ?::atividade_status, ?, ?, ?)
            RETURNING id, status, registrado_em, validado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, a.getAtividadeId());
            st.setObject(2, a.getAssociadoId());
            st.setString(3, a.getStatus() != null ? a.getStatus() : "pendente");
            st.setString(4, a.getDescricaoRegistro());
            Array midiaArray = con.createArrayOf("text",
                a.getMidiaUrl() != null ? a.getMidiaUrl() : new String[0]);
            st.setArray(5, midiaArray);
            st.setObject(6, a.getDataRealizacao());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                a.setId((UUID) rs.getObject("id"));
                a.setStatus(rs.getString("status"));
                a.setRegistradoEm(rs.getObject("registrado_em", java.time.OffsetDateTime.class));
                a.setValidadoEm(rs.getObject("validado_em", java.time.OffsetDateTime.class));
            }
        }
        return a;
    }

    public AtribuicaoAtividade buscarAtivaPorAtividadeEAssociado(UUID atividadeId, UUID associadoId)
            throws SQLException {
        String sql = "SELECT " + COLS +
            " FROM atribuicoes_atividade WHERE atividade_id = ? AND associado_id = ?" +
            " ORDER BY registrado_em DESC LIMIT 1";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, atividadeId);
            st.setObject(2, associadoId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public boolean atualizar(AtribuicaoAtividade a) throws SQLException {
        // COALESCE mantém o valor atual do banco quando o campo não é enviado (null)
        String sql = """
            UPDATE atribuicoes_atividade SET
              status             = COALESCE(?::atividade_status, status),
              registrado_em      = COALESCE(?, registrado_em),
              descricao_registro = COALESCE(?, descricao_registro),
              midia_url          = COALESCE(?, midia_url),
              validado_por       = COALESCE(?, validado_por),
              validado_em        = COALESCE(?, validado_em),
              feedback           = COALESCE(?, feedback),
              data_realizacao    = COALESCE(?, data_realizacao)
            WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, a.getStatus());
            st.setObject(2, a.getRegistradoEm());
            st.setString(3, a.getDescricaoRegistro());
            Array midiaArray = a.getMidiaUrl() != null && a.getMidiaUrl().length > 0
                ? con.createArrayOf("text", a.getMidiaUrl()) : null;
            st.setArray(4, midiaArray);
            st.setObject(5, a.getValidadoPor());
            st.setObject(6, a.getValidadoEm());
            st.setString(7, a.getFeedback());
            st.setObject(8, a.getDataRealizacao());
            st.setObject(9, a.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM atribuicoes_atividade WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private AtribuicaoAtividade mapear(ResultSet rs) throws SQLException {
        AtribuicaoAtividade a = new AtribuicaoAtividade();
        a.setId((UUID) rs.getObject("id"));
        a.setAtividadeId((UUID) rs.getObject("atividade_id"));
        a.setAssociadoId((UUID) rs.getObject("associado_id"));
        a.setStatus(rs.getString("status"));
        a.setRegistradoEm(rs.getObject("registrado_em", java.time.OffsetDateTime.class));
        a.setDescricaoRegistro(rs.getString("descricao_registro"));
        // Ler text[] do PostgreSQL
        Array midiaArr = rs.getArray("midia_url");
        if (midiaArr != null) a.setMidiaUrl((String[]) midiaArr.getArray());
        a.setValidadoPor((UUID) rs.getObject("validado_por"));
        a.setValidadoEm(rs.getObject("validado_em", java.time.OffsetDateTime.class));
        a.setFeedback(rs.getString("feedback"));
        Date dr = rs.getDate("data_realizacao");
        if (dr != null) a.setDataRealizacao(dr.toLocalDate());
        return a;
    }
}
