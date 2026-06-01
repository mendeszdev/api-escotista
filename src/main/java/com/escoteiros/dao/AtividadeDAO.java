package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Atividade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AtividadeDAO {

    private static final String COLS = """
        id, acao_educativa_id, criado_por, alcateia_id,
        e_personalizada, nome_personalizado, descricao_personalizada,
        aprovada_por, data_limite, criado_em, atualizado_em
        """;

    public List<Atividade> listarTodos() throws SQLException {
        List<Atividade> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM atividades ORDER BY criado_em DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Atividade> listarPorAlcateia(UUID alcateiaId) throws SQLException {
        List<Atividade> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM atividades WHERE alcateia_id = ? ORDER BY criado_em DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, alcateiaId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Atividade> listarPorBloco(UUID blocoId) throws SQLException {
        List<Atividade> lista = new ArrayList<>();
        String sql = """
            SELECT a.id, a.acao_educativa_id, a.criado_por, a.alcateia_id,
                   a.e_personalizada, a.nome_personalizado, a.descricao_personalizada,
                   a.aprovada_por, a.data_limite, a.criado_em, a.atualizado_em
            FROM atividades a
            JOIN acoes_educativas ae ON a.acao_educativa_id = ae.id
            WHERE ae.bloco_id = ?
            ORDER BY a.criado_em DESC
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, blocoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Atividade buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM atividades WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Atividade inserir(Atividade a) throws SQLException {
        String sql = """
            INSERT INTO atividades
              (acao_educativa_id, criado_por, alcateia_id, e_personalizada,
               nome_personalizado, descricao_personalizada, aprovada_por, data_limite)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, a.getAcaoEducativaId());
            st.setObject(2, a.getCriadoPor());
            st.setObject(3, a.getAlcateiaId());
            st.setBoolean(4, a.isEPersonalizada());
            st.setString(5, a.getNomePersonalizado());
            st.setString(6, a.getDescricaoPersonalizada());
            st.setObject(7, a.getAprovadaPor());
            st.setObject(8, a.getDataLimite());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                a.setId((UUID) rs.getObject("id"));
                a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        return a;
    }

    public boolean atualizar(Atividade a) throws SQLException {
        String sql = """
            UPDATE atividades SET
              e_personalizada = ?, nome_personalizado = ?,
              descricao_personalizada = ?, aprovada_por = ?, data_limite = ?
            WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setBoolean(1, a.isEPersonalizada());
            st.setString(2, a.getNomePersonalizado());
            st.setString(3, a.getDescricaoPersonalizada());
            st.setObject(4, a.getAprovadaPor());
            st.setObject(5, a.getDataLimite());
            st.setObject(6, a.getId());
            return st.executeUpdate() > 0;
        }
    }

    public Atividade buscarOuCriarParaAcao(UUID acaoId, UUID alcateiaId, UUID criadoPor)
            throws SQLException {
        String find = "SELECT " + COLS +
            " FROM atividades WHERE acao_educativa_id = ? AND alcateia_id = ? LIMIT 1";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(find)) {
            st.setObject(1, acaoId);
            st.setObject(2, alcateiaId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        Atividade nova = new Atividade();
        nova.setAcaoEducativaId(acaoId);
        nova.setAlcateiaId(alcateiaId);
        nova.setCriadoPor(criadoPor);
        nova.setEPersonalizada(false);
        return inserir(nova);
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM atividades WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private Atividade mapear(ResultSet rs) throws SQLException {
        Atividade a = new Atividade();
        a.setId((UUID) rs.getObject("id"));
        a.setAcaoEducativaId((UUID) rs.getObject("acao_educativa_id"));
        a.setCriadoPor((UUID) rs.getObject("criado_por"));
        a.setAlcateiaId((UUID) rs.getObject("alcateia_id"));
        a.setEPersonalizada(rs.getBoolean("e_personalizada"));
        a.setNomePersonalizado(rs.getString("nome_personalizado"));
        a.setDescricaoPersonalizada(rs.getString("descricao_personalizada"));
        a.setAprovadaPor((UUID) rs.getObject("aprovada_por"));
        Date dl = rs.getDate("data_limite");
        if (dl != null) a.setDataLimite(dl.toLocalDate());
        a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return a;
    }
}
