package com.escoteiros.servlet;

import com.escoteiros.config.DatabaseConfig;
import com.escoteiros.dao.AcaoEducativaDAO;
import com.escoteiros.dao.AtribuicaoAtividadeDAO;
import com.escoteiros.dao.AtividadeDAO;
import com.escoteiros.model.AcaoEducativa;
import com.escoteiros.model.AtribuicaoAtividade;
import com.escoteiros.model.Atividade;
import com.escoteiros.util.BaseServlet;
import com.escoteiros.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Endpoints:
 *   GET    /api/atribuicoes                           → lista todas
 *   GET    /api/atribuicoes/{id}                      → busca por id
 *   GET    /api/atribuicoes?associado={uuid}          → filtra por associado
 *   GET    /api/atribuicoes?atividade={uuid}          → filtra por atividade
 *   POST   /api/atribuicoes                           → cria atribuição direta (escotista)
 *   POST   /api/atribuicoes/registrar                 → lobinho registra conclusão de ação
 *   POST   /api/atribuicoes/atribuir-bloco            → escotista atribui bloco inteiro a lobinho
 *   PUT    /api/atribuicoes/{id}                      → atualiza (aprovar, recusar)
 *   DELETE /api/atribuicoes/{id}                      → remove
 */
@WebServlet("/api/atribuicoes/*")
public class AtribuicaoAtividadeServlet extends BaseServlet {

    private final AtribuicaoAtividadeDAO dao         = new AtribuicaoAtividadeDAO();
    private final AtividadeDAO           atividadeDao = new AtividadeDAO();
    private final AcaoEducativaDAO       acaoDao      = new AcaoEducativaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String associadoParam = req.getParameter("associado");
                String atividadeParam = req.getParameter("atividade");

                List<AtribuicaoAtividade> lista;
                if (associadoParam != null) {
                    lista = dao.listarPorAssociado(UUID.fromString(associadoParam));
                } else if (atividadeParam != null) {
                    lista = dao.listarPorAtividade(UUID.fromString(atividadeParam));
                } else {
                    lista = dao.listarTodos();
                }
                res.getWriter().print(gson.toJson(lista));
            } else {
                AtribuicaoAtividade a = dao.buscarPorId(UUID.fromString(id));
                if (a != null) res.getWriter().print(gson.toJson(a));
                else erro(res, 404, "Atribuição não encontrada");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String path = extrairId(req);
        try {
            if ("registrar".equals(path)) {
                handleRegistrarLobinho(req, res);
            } else if ("atribuir-bloco".equals(path)) {
                handleAtribuirBloco(req, res);
            } else if (path == null) {
                AtribuicaoAtividade a = gson.fromJson(lerBody(req), AtribuicaoAtividade.class);
                AtribuicaoAtividade criada = dao.inserir(a);
                res.setStatus(201);
                res.getWriter().print(gson.toJson(criada));
            } else {
                erro(res, 404, "Rota não encontrada");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            AtribuicaoAtividade a = gson.fromJson(lerBody(req), AtribuicaoAtividade.class);
            a.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(a);
            if (ok) ok(res, "Atribuição atualizada com sucesso");
            else erro(res, 404, "Atribuição não encontrada");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            boolean ok = dao.deletar(UUID.fromString(id));
            if (ok) ok(res, "Atribuição removida com sucesso");
            else erro(res, 404, "Atribuição não encontrada");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Lobinho registra a conclusão de uma ação educativa
    //
    // POST /api/atribuicoes/registrar
    // Body: { "acaoEducativaId": "uuid", "dataRealizacao": "YYYY-MM-DD",
    //         "descricaoRegistro": "opcional" }
    //
    // Fluxo:
    //   1. Extrai associado_id do JWT
    //   2. Busca alcateia_id via matilha do lobinho
    //   3. Encontra ou cria atividade para (acao, alcateia)
    //   4. Verifica se já existe atribuição pendente/concluída
    //   5. Cria atribuição com status=pendente
    // ──────────────────────────────────────────────────────────────────
    private void handleRegistrarLobinho(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        UUID associadoId = extrairAssociadoIdDoJwt(req);
        if (associadoId == null) { erro(res, 401, "Token inválido"); return; }

        Map<?, ?> body = gson.fromJson(lerBody(req), Map.class);
        if (body == null || body.get("acaoEducativaId") == null) {
            erro(res, 400, "acaoEducativaId é obrigatório");
            return;
        }

        UUID acaoId;
        LocalDate dataRealizacao = null;
        try {
            acaoId = UUID.fromString((String) body.get("acaoEducativaId"));
            String dr = (String) body.get("dataRealizacao");
            if (dr != null && !dr.isBlank()) dataRealizacao = LocalDate.parse(dr);
        } catch (Exception e) {
            erro(res, 400, "Dados inválidos: " + e.getMessage());
            return;
        }
        String descricao = (String) body.get("descricaoRegistro");

        try {
            UUID alcateiaId = buscarAlcateiaDoAssociado(associadoId);
            if (alcateiaId == null) {
                erro(res, 400, "Lobinho não está vinculado a nenhuma alcateia ativa");
                return;
            }

            Atividade atividade = atividadeDao.buscarOuCriarParaAcao(acaoId, alcateiaId, associadoId);

            AtribuicaoAtividade existente =
                dao.buscarAtivaPorAtividadeEAssociado(atividade.getId(), associadoId);
            if (existente != null && !"recusada".equals(existente.getStatus())) {
                erro(res, 409, "Atividade já registrada com status: " + existente.getStatus());
                return;
            }

            AtribuicaoAtividade nova = new AtribuicaoAtividade();
            nova.setAtividadeId(atividade.getId());
            nova.setAssociadoId(associadoId);
            nova.setStatus("pendente");
            nova.setDataRealizacao(dataRealizacao);
            nova.setDescricaoRegistro(descricao);

            AtribuicaoAtividade criada = dao.inserir(nova);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(criada));

        } catch (SQLException e) {
            erro(res, 500, e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Escotista atribui todas as ações de um bloco a um lobinho
    //
    // POST /api/atribuicoes/atribuir-bloco
    // Body: { "blocoId": "uuid", "associadoId": "uuid" }
    //
    // Para cada acao_educativa do bloco:
    //   - encontra/cria atividade para (acao, alcateia do lobinho)
    //   - cria atribuição com status=pendente (pula se já existe ativa)
    // ──────────────────────────────────────────────────────────────────
    private void handleAtribuirBloco(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        Map<?, ?> body = gson.fromJson(lerBody(req), Map.class);
        if (body == null || body.get("blocoId") == null || body.get("associadoId") == null) {
            erro(res, 400, "blocoId e associadoId são obrigatórios");
            return;
        }

        UUID blocoId, associadoId;
        try {
            blocoId     = UUID.fromString((String) body.get("blocoId"));
            associadoId = UUID.fromString((String) body.get("associadoId"));
        } catch (Exception e) {
            erro(res, 400, "UUID inválido: " + e.getMessage());
            return;
        }

        UUID escotistaId = extrairAssociadoIdDoJwt(req);

        try {
            UUID alcateiaId = buscarAlcateiaDoAssociado(associadoId);
            if (alcateiaId == null) {
                erro(res, 400, "Associado não está vinculado a nenhuma alcateia ativa");
                return;
            }

            List<AcaoEducativa> acoes = acaoDao.listarPorBloco(blocoId);
            if (acoes.isEmpty()) {
                erro(res, 404, "Nenhuma ação educativa encontrada para este bloco");
                return;
            }

            List<AtribuicaoAtividade> criadas = new ArrayList<>();
            for (AcaoEducativa acao : acoes) {
                Atividade atividade =
                    atividadeDao.buscarOuCriarParaAcao(acao.getId(), alcateiaId, escotistaId);

                AtribuicaoAtividade existente =
                    dao.buscarAtivaPorAtividadeEAssociado(atividade.getId(), associadoId);
                if (existente != null && !"recusada".equals(existente.getStatus())) continue;

                AtribuicaoAtividade nova = new AtribuicaoAtividade();
                nova.setAtividadeId(atividade.getId());
                nova.setAssociadoId(associadoId);
                nova.setStatus("pendente");
                criadas.add(dao.inserir(nova));
            }

            res.setStatus(201);
            res.getWriter().print(gson.toJson(criadas));

        } catch (SQLException e) {
            erro(res, 500, e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────

    private UUID extrairAssociadoIdDoJwt(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        try {
            Claims claims = JwtUtil.validar(auth.substring(7).trim());
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    private UUID buscarAlcateiaDoAssociado(UUID associadoId) throws SQLException {
        String sql = """
            SELECT m.alcateia_id
            FROM membros_matilha mm
            JOIN matilhas m ON m.id = mm.matilha_id
            WHERE mm.associado_id = ? AND mm.ativo = true
            LIMIT 1
            """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setObject(1, associadoId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return (UUID) rs.getObject("alcateia_id");
        }
        return null;
    }
}
