package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Alcateia;

import java.sql.*;
import java.util.*;
import java.util.UUID;

public class AlcateiaDAO {

    public List<Alcateia> listarTodos() throws SQLException {
        List<Alcateia> lista = new ArrayList<>();
        String sql = "SELECT id, grupo_escoteiro_id, nome, descricao, status, criado_em, atualizado_em FROM alcateias ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Alcateia> listarPorGrupo(UUID grupoId) throws SQLException {
        List<Alcateia> lista = new ArrayList<>();
        String sql = "SELECT id, grupo_escoteiro_id, nome, descricao, status, criado_em, atualizado_em FROM alcateias WHERE grupo_escoteiro_id = ? ORDER BY nome";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, grupoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Alcateia buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT id, grupo_escoteiro_id, nome, descricao, status, criado_em, atualizado_em FROM alcateias WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /**
     * Retorna alcateias com matilhas enriquecidas:
     *   totalLobinhos, progressoMedio e chefe (primeiro escotista ativo).
     * Se grupoId for null, retorna todas.
     */
    public List<Map<String, Object>> listarComStats(UUID grupoId) throws SQLException {
        String sqlAlcateias = grupoId != null
            ? "SELECT id, nome, descricao, status FROM alcateias WHERE grupo_escoteiro_id = ? ORDER BY nome"
            : "SELECT id, nome, descricao, status FROM alcateias ORDER BY nome";

        String sqlMatilhas = """
            SELECT m.id, m.nome, m.cor, m.status,
                   COUNT(mm.id) FILTER (WHERE mm.ativo = true)                              AS total_lobinhos,
                   COALESCE(AVG(p.percentual_concluido) FILTER (WHERE mm.ativo = true), 0)  AS progresso_medio
            FROM matilhas m
            LEFT JOIN membros_matilha mm ON mm.matilha_id = m.id
            LEFT JOIN progressoes      p  ON p.associado_id = mm.associado_id
            WHERE m.alcateia_id = ?
            GROUP BY m.id, m.nome, m.cor, m.status
            ORDER BY m.nome
            """;

        String sqlChefe = """
            SELECT a.nome_completo
            FROM escotistas_alcateias ea
            JOIN associados a ON a.id = ea.associado_id
            WHERE ea.alcateia_id = ? AND ea.ativo = true
            LIMIT 1
            """;

        List<Map<String, Object>> resultado = new ArrayList<>();

        try (Connection con = DatabaseConfig.getConnection()) {
            // 1. Alcateias
            PreparedStatement stAlc = con.prepareStatement(sqlAlcateias);
            if (grupoId != null) stAlc.setObject(1, grupoId);
            ResultSet rsAlc = stAlc.executeQuery();

            while (rsAlc.next()) {
                UUID alcId = (UUID) rsAlc.getObject("id");

                Map<String, Object> alcateia = new LinkedHashMap<>();
                alcateia.put("id",      alcId.toString());
                alcateia.put("nome",    rsAlc.getString("nome"));
                alcateia.put("descricao", rsAlc.getString("descricao"));
                alcateia.put("status",  rsAlc.getString("status"));

                // 2. Chefe da alcateia
                PreparedStatement stChefe = con.prepareStatement(sqlChefe);
                stChefe.setObject(1, alcId);
                ResultSet rsChefe = stChefe.executeQuery();
                String chefe = rsChefe.next() ? rsChefe.getString("nome_completo") : null;

                // 3. Matilhas com stats
                PreparedStatement stMat = con.prepareStatement(sqlMatilhas);
                stMat.setObject(1, alcId);
                ResultSet rsMat = stMat.executeQuery();

                List<Map<String, Object>> matilhas = new ArrayList<>();
                while (rsMat.next()) {
                    Map<String, Object> mat = new LinkedHashMap<>();
                    mat.put("id",            ((UUID) rsMat.getObject("id")).toString());
                    mat.put("nome",          rsMat.getString("nome"));
                    mat.put("cor",           rsMat.getString("cor"));
                    mat.put("status",        rsMat.getString("status"));
                    mat.put("totalLobinhos", rsMat.getInt("total_lobinhos"));
                    mat.put("progressoMedio", rsMat.getDouble("progresso_medio"));
                    mat.put("chefe",         chefe);
                    matilhas.add(mat);
                }

                alcateia.put("matilhas", matilhas);
                resultado.add(alcateia);
            }
        }
        return resultado;
    }

    public Alcateia inserir(Alcateia a) throws SQLException {
        String sql = """
            INSERT INTO alcateias (grupo_escoteiro_id, nome, descricao, status)
            VALUES (?, ?, ?, ?::status_geral)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, a.getGrupoEscoteiroId());
            st.setString(2, a.getNome());
            st.setString(3, a.getDescricao());
            st.setString(4, a.getStatus() != null ? a.getStatus() : "ativo");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                a.setId((UUID) rs.getObject("id"));
                a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        return a;
    }

    public boolean atualizar(Alcateia a) throws SQLException {
        String sql = "UPDATE alcateias SET nome = ?, descricao = ?, status = ?::status_geral WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, a.getNome());
            st.setString(2, a.getDescricao());
            st.setString(3, a.getStatus());
            st.setObject(4, a.getId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM alcateias WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    private Alcateia mapear(ResultSet rs) throws SQLException {
        Alcateia a = new Alcateia();
        a.setId((UUID) rs.getObject("id"));
        a.setGrupoEscoteiroId((UUID) rs.getObject("grupo_escoteiro_id"));
        a.setNome(rs.getString("nome"));
        a.setDescricao(rs.getString("descricao"));
        a.setStatus(rs.getString("status"));
        a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return a;
    }
}
