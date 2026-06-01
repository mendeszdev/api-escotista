package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.AtribuicaoDetalhada;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AtribuicaoDetalhadaDAO {

    private static final String SQL_BASE = """
        SELECT
            aa.id, aa.atividade_id, aa.associado_id, aa.status,
            aa.registrado_em, aa.descricao_registro, aa.midia_url,
            aa.validado_por, aa.validado_em, aa.feedback,
            aa.data_realizacao,
            ae.id                AS acao_educativa_id,
            ae.tipo::text        AS acao_tipo,
            asc_.nome_completo   AS associado_nome,
            COALESCE(at.nome_personalizado, ae.nome) AS atividade_nome,
            ba.nome              AS bloco_nome,
            ba.id                AS bloco_id,
            e.nome               AS eixo_nome,
            e.id                 AS eixo_id,
            at.data_limite
        FROM atribuicoes_atividade aa
        JOIN associados            asc_ ON asc_.id = aa.associado_id
        JOIN atividades            at   ON at.id   = aa.atividade_id
        LEFT JOIN acoes_educativas ae   ON ae.id   = at.acao_educativa_id
        LEFT JOIN blocos_aprendizagem ba ON ba.id  = ae.bloco_id
        LEFT JOIN eixos            e    ON e.id    = ba.eixo_id
        """;

    public List<AtribuicaoDetalhada> listarPorAlcateia(UUID alcateiaId) throws SQLException {
        String sql = SQL_BASE + " WHERE at.alcateia_id = ? ORDER BY aa.registrado_em DESC NULLS LAST";
        List<AtribuicaoDetalhada> lista = new ArrayList<>();
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, alcateiaId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<AtribuicaoDetalhada> listarPorAssociado(UUID associadoId) throws SQLException {
        String sql = SQL_BASE + " WHERE aa.associado_id = ? ORDER BY aa.registrado_em DESC NULLS LAST";
        List<AtribuicaoDetalhada> lista = new ArrayList<>();
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private AtribuicaoDetalhada mapear(ResultSet rs) throws SQLException {
        AtribuicaoDetalhada a = new AtribuicaoDetalhada();
        a.setId((UUID) rs.getObject("id"));
        a.setAtividadeId((UUID) rs.getObject("atividade_id"));
        a.setAssociadoId((UUID) rs.getObject("associado_id"));
        a.setStatus(rs.getString("status"));
        a.setRegistradoEm(rs.getObject("registrado_em", java.time.OffsetDateTime.class));
        a.setDescricaoRegistro(rs.getString("descricao_registro"));
        Array midiaArr = rs.getArray("midia_url");
        if (midiaArr != null) a.setMidiaUrl((String[]) midiaArr.getArray());
        a.setValidadoPor((UUID) rs.getObject("validado_por"));
        a.setValidadoEm(rs.getObject("validado_em", java.time.OffsetDateTime.class));
        a.setFeedback(rs.getString("feedback"));
        Date drz = rs.getDate("data_realizacao");
        if (drz != null) a.setDataRealizacao(drz.toLocalDate());
        a.setAcaoEducativaId((UUID) rs.getObject("acao_educativa_id"));
        a.setAcaoTipo(rs.getString("acao_tipo"));
        a.setAssociadoNome(rs.getString("associado_nome"));
        a.setAtividadeNome(rs.getString("atividade_nome"));
        a.setBlocoNome(rs.getString("bloco_nome"));
        a.setBlocoId((UUID) rs.getObject("bloco_id"));
        a.setEixoNome(rs.getString("eixo_nome"));
        a.setEixoId((UUID) rs.getObject("eixo_id"));
        Date dl = rs.getDate("data_limite");
        if (dl != null) a.setDataLimite(dl.toLocalDate());
        return a;
    }
}
