package com.escoteiros.servlet;

import com.escoteiros.dao.AtribuicaoAtividadeDAO;
import com.escoteiros.model.AtribuicaoAtividade;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/atribuicoes                           → lista todas
 *   GET    /api/atribuicoes/{id}                      → busca por id
 *   GET    /api/atribuicoes?associado={uuid}          → filtra por associado
 *   GET    /api/atribuicoes?atividade={uuid}          → filtra por atividade
 *   POST   /api/atribuicoes                           → cria nova atribuição
 *   PUT    /api/atribuicoes/{id}                      → atualiza (registrar, validar)
 *   DELETE /api/atribuicoes/{id}                      → remove
 */
@WebServlet("/api/atribuicoes/*")
public class AtribuicaoAtividadeServlet extends BaseServlet {

    private final AtribuicaoAtividadeDAO dao = new AtribuicaoAtividadeDAO();

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
        try {
            AtribuicaoAtividade a = gson.fromJson(lerBody(req), AtribuicaoAtividade.class);
            AtribuicaoAtividade criada = dao.inserir(a);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(criada));
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
}
