package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.AssociadoDistintivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssociadoDistintivoDAO {

    private static final String SQL_JOIN = """
        SELECT
            ad.id, ad.associado_id, ad.distintivo_id,
            ad.observacao, ad.atribuido_em, ad.atribuido_por,
            d.nome         AS distintivo_nome,
            d.descricao    AS distintivo_descricao,
            d.icone        AS distintivo_icone,
            d.cor_hex      AS distintivo_cor_hex,
            d.e_especialidade AS distintivo_e_especialidade
        FROM associado_distintivos ad
        JOIN distintivos d ON d.id = ad.distintivo_id
        """;

    public List<AssociadoDistintivo> listarPorAssociado(UUID associadoId) throws SQLException {
        List<AssociadoDistintivo> lista = new ArrayList<>();
        String sql = SQL_JOIN + " WHERE ad.associado_id = ? ORDER BY ad.atribuido_em DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<AssociadoDistintivo> listarPorAlcateia(UUID alcateiaId) throws SQLException {
        List<AssociadoDistintivo> lista = new ArrayList<>();
        String sql = SQL_JOIN + """
             JOIN associados a ON a.id = ad.associado_id
             JOIN membros_matilha mm ON mm.associado_id = a.id AND mm.ativo = true
             JOIN matilhas m ON m.id = mm.matilha_id
            WHERE m.alcateia_id = ?
            ORDER BY ad.atribuido_em DESC
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, alcateiaId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public AssociadoDistintivo buscarPorId(UUID id) throws SQLException {
        String sql = SQL_JOIN + " WHERE ad.id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public AssociadoDistintivo inserir(AssociadoDistintivo ad) throws SQLException {
        String sql = """
            INSERT INTO associado_distintivos
              (associado_id, distintivo_id, observacao, atribuido_por)
            VALUES (?, ?, ?, ?)
            RETURNING id, atribuido_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, ad.getAssociadoId());
            st.setObject(2, ad.getDistintivoId());
            st.setString(3, ad.getObservacao());
            st.setObject(4, ad.getAtribuidoPor());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                ad.setId((UUID) rs.getObject("id"));
                ad.setAtribuidoEm(rs.getObject("atribuido_em", java.time.OffsetDateTime.class));
            }
        }
        return buscarPorId(ad.getId());
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM associado_distintivos WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private AssociadoDistintivo mapear(ResultSet rs) throws SQLException {
        AssociadoDistintivo ad = new AssociadoDistintivo();
        ad.setId((UUID) rs.getObject("id"));
        ad.setAssociadoId((UUID) rs.getObject("associado_id"));
        ad.setDistintivoId((UUID) rs.getObject("distintivo_id"));
        ad.setObservacao(rs.getString("observacao"));
        ad.setAtribuidoEm(rs.getObject("atribuido_em", java.time.OffsetDateTime.class));
        ad.setAtribuidoPor((UUID) rs.getObject("atribuido_por"));
        ad.setDistintivoNome(rs.getString("distintivo_nome"));
        ad.setDistintivoDescricao(rs.getString("distintivo_descricao"));
        ad.setDistintivoIcone(rs.getString("distintivo_icone"));
        ad.setDistintivoCorHex(rs.getString("distintivo_cor_hex"));
        ad.setDistintivoEEspecialidade(rs.getBoolean("distintivo_e_especialidade"));
        return ad;
    }
}
