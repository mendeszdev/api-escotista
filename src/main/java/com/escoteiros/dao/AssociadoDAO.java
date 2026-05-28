package com.escoteiros.dao;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.model.Associado;
import com.escoteiros.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssociadoDAO {

    // Colunas retornadas em SELECT (nunca retorna senha_hash)
    private static final String COLUNAS = """
        id, grupo_escoteiro_id, matricula, perfil, nome_completo, nome_escoteiro,
        data_nascimento, genero, estado_civil, cpf, rg, passaporte, email, telefone,
        cep, logradouro, numero_end, bairro, cidade, estado, foto_url,
        status, documentos_validados, criado_em, atualizado_em
        """;

    // ── LISTAR TODOS ────────────────────────────────────────────────────────
    public List<Associado> listarTodos() throws SQLException {
        List<Associado> lista = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM associados ORDER BY nome_completo";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── LISTAR POR GRUPO ────────────────────────────────────────────────────
    public List<Associado> listarPorGrupo(UUID grupoId) throws SQLException {
        List<Associado> lista = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM associados WHERE grupo_escoteiro_id = ? ORDER BY nome_completo";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, grupoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── LISTAR POR PERFIL ───────────────────────────────────────────────────
    public List<Associado> listarPorPerfil(String perfil) throws SQLException {
        List<Associado> lista = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM associados WHERE perfil = ?::perfil_tipo ORDER BY nome_completo";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, perfil);
            ResultSet rs = st.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── BUSCAR POR ID ───────────────────────────────────────────────────────
    public Associado buscarPorId(UUID id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM associados WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Associado autenticar(String matricula, String senha) throws SQLException {
        String sql = """
        SELECT id, grupo_escoteiro_id, matricula, senha_hash, perfil,
               nome_completo, nome_escoteiro, data_nascimento, genero, estado_civil,
               cpf, rg, passaporte, email, telefone, cep, logradouro, numero_end,
               bairro, cidade, estado, foto_url, status, documentos_validados,
               criado_em, atualizado_em
        FROM associados WHERE matricula = ? AND status = 'ativo'
        """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, matricula);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return null;
            String senhaHashSalva = rs.getString("senha_hash");
            if (!PasswordUtil.verificar(senha, senhaHashSalva)) return null;
            Associado a = mapear(rs);
            a.setSenhaHash(null);
            return a;
        }
    }

    // ── INSERIR ─────────────────────────────────────────────────────────────
    public Associado inserir(Associado a) throws SQLException {
        String sql = """
            INSERT INTO associados
              (grupo_escoteiro_id, matricula, senha_hash, perfil, nome_completo,
               nome_escoteiro, data_nascimento, genero, estado_civil, cpf, rg,
               passaporte, email, telefone, cep, logradouro, numero_end,
               bairro, cidade, estado, foto_url, status)
            VALUES
              (?, ?, ?, ?::perfil_tipo, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::status_geral)
            RETURNING id, criado_em, atualizado_em
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1,  a.getGrupoEscotelroId());
            st.setString(2,  a.getMatricula());
            st.setString(3,  a.getSenhaHash()); // já deve vir com hash do controller
            st.setString(4,  a.getPerfil());
            st.setString(5,  a.getNomeCompleto());
            st.setString(6,  a.getNomeEscoteiro());
            st.setObject(7,  a.getDataNascimento());
            st.setString(8,  a.getGenero());
            st.setString(9,  a.getEstadoCivil());
            st.setString(10, a.getCpf());
            st.setString(11, a.getRg());
            st.setString(12, a.getPassaporte());
            st.setString(13, a.getEmail());
            st.setString(14, a.getTelefone());
            st.setString(15, a.getCep());
            st.setString(16, a.getLogradouro());
            st.setString(17, a.getNumeroEnd());
            st.setString(18, a.getBairro());
            st.setString(19, a.getCidade());
            st.setString(20, a.getEstado());
            st.setString(21, a.getFotoUrl());
            st.setString(22, a.getStatus() != null ? a.getStatus() : "ativo");

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                a.setId((UUID) rs.getObject("id"));
                a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
                a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
            }
        }
        a.setSenhaHash(null); // nunca retornar hash
        return a;
    }

    // ── ATUALIZAR ───────────────────────────────────────────────────────────
    public boolean atualizar(Associado a) throws SQLException {
        String sql = """
            UPDATE associados SET
              nome_completo = ?, nome_escoteiro = ?, genero = ?, estado_civil = ?,
              email = ?, telefone = ?, cep = ?, logradouro = ?, numero_end = ?,
              bairro = ?, cidade = ?, estado = ?, foto_url = ?,
              status = ?::status_geral, documentos_validados = ?
            WHERE id = ?
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1,  a.getNomeCompleto());
            st.setString(2,  a.getNomeEscoteiro());
            st.setString(3,  a.getGenero());
            st.setString(4,  a.getEstadoCivil());
            st.setString(5,  a.getEmail());
            st.setString(6,  a.getTelefone());
            st.setString(7,  a.getCep());
            st.setString(8,  a.getLogradouro());
            st.setString(9,  a.getNumeroEnd());
            st.setString(10, a.getBairro());
            st.setString(11, a.getCidade());
            st.setString(12, a.getEstado());
            st.setString(13, a.getFotoUrl());
            st.setString(14, a.getStatus());
            st.setBoolean(15, a.isDocumentosValidados());
            st.setObject(16, a.getId());
            return st.executeUpdate() > 0;
        }
    }

    // ── DELETAR ─────────────────────────────────────────────────────────────
    public boolean deletar(UUID id) throws SQLException {
        String sql = "DELETE FROM associados WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        }
    }

    // ── MAPEAMENTO ──────────────────────────────────────────────────────────
    private Associado mapear(ResultSet rs) throws SQLException {
        Associado a = new Associado();
        a.setId((UUID) rs.getObject("id"));
        a.setGrupoEscotelroId((UUID) rs.getObject("grupo_escoteiro_id"));
        a.setMatricula(rs.getString("matricula"));
        a.setPerfil(rs.getString("perfil"));
        a.setNomeCompleto(rs.getString("nome_completo"));
        a.setNomeEscoteiro(rs.getString("nome_escoteiro"));
        Date dn = rs.getDate("data_nascimento");
        if (dn != null) a.setDataNascimento(dn.toLocalDate());
        a.setGenero(rs.getString("genero"));
        a.setEstadoCivil(rs.getString("estado_civil"));
        a.setCpf(rs.getString("cpf"));
        a.setRg(rs.getString("rg"));
        a.setPassaporte(rs.getString("passaporte"));
        a.setEmail(rs.getString("email"));
        a.setTelefone(rs.getString("telefone"));
        a.setCep(rs.getString("cep"));
        a.setLogradouro(rs.getString("logradouro"));
        a.setNumeroEnd(rs.getString("numero_end"));
        a.setBairro(rs.getString("bairro"));
        a.setCidade(rs.getString("cidade"));
        a.setEstado(rs.getString("estado"));
        a.setFotoUrl(rs.getString("foto_url"));
        a.setStatus(rs.getString("status"));
        a.setDocumentosValidados(rs.getBoolean("documentos_validados"));
        a.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        a.setAtualizadoEm(rs.getObject("atualizado_em", java.time.OffsetDateTime.class));
        return a;
    }
}
