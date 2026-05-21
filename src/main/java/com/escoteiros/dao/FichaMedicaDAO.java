package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.FichaMedica;

import java.sql.*;
import java.util.UUID;

public class FichaMedicaDAO {

    public FichaMedica buscarPorAssociado(UUID associadoId) throws SQLException {
        String sql = "SELECT id, associado_id, tipo_sanguineo, alergias, condicoes, medicamentos, observacoes, criado_em, atualizado_em FROM fichas_medicas WHERE associado_id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public FichaMedica salvarOuAtualizar(FichaMedica f) throws SQLException {
        String sql = """
            INSERT INTO fichas_medicas (associado_id, tipo_sanguineo, alergias, condicoes, medicamentos, observacoes)
            VALUES (?, ?::tipo_sanguineo, ?, ?, ?, ?)
            ON CONFLICT (associado_id) DO UPDATE SET
              tipo_sanguineo = EXCLUDED.tipo_sanguineo,
              alergias       = EXCLUDED.alergias,
              condicoes      = EXCLUDED.condicoes,
              medicamentos   = EXCLUDED.medicamentos,
              observacoes    = EXCLUDED.observacoes
            RETURNING id
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, f.getAssociadoId());
            st.setString(2, f.getTipoSanguineo());
            st.setArray(3, con.createArrayOf("text", nvl(f.getAlergias())));
            st.setArray(4, con.createArrayOf("text", nvl(f.getCondicoes())));
            st.setArray(5, con.createArrayOf("text", nvl(f.getMedicamentos())));
            st.setString(6, f.getObservacoes());
            ResultSet rs = st.executeQuery();
            if (rs.next()) f.setId((UUID) rs.getObject("id"));
        }
        return f;
    }

    private String[] nvl(String[] arr) { return arr != null ? arr : new String[0]; }

    private FichaMedica mapear(ResultSet rs) throws SQLException {
        FichaMedica f = new FichaMedica();
        f.setId((UUID) rs.getObject("id"));
        f.setAssociadoId((UUID) rs.getObject("associado_id"));
        f.setTipoSanguineo(rs.getString("tipo_sanguineo"));
        Array al = rs.getArray("alergias"); if (al != null) f.setAlergias((String[]) al.getArray());
        Array co = rs.getArray("condicoes"); if (co != null) f.setCondicoes((String[]) co.getArray());
        Array me = rs.getArray("medicamentos"); if (me != null) f.setMedicamentos((String[]) me.getArray());
        f.setObservacoes(rs.getString("observacoes"));
        f.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        f.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return f;
    }
}
