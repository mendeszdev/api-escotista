package com.escoteiros.servlet;

import com.escoteiros.dao.AcaoEducativaDAO;
import com.escoteiros.model.AcaoEducativa;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/acoes-educativas                  → lista todas
 *   GET    /api/acoes-educativas/{id}             → busca por id
 *   GET    /api/acoes-educativas?bloco={uuid}     → filtra por bloco de aprendizagem
 *   POST   /api/acoes-educativas                  → cria nova
 *   PUT    /api/acoes-educativas/{id}             → atualiza
 *   DELETE /api/acoes-educativas/{id}             → remove
 */
@WebServlet("/api/acoes-educativas/*")
public class AcaoEducativaServlet extends BaseServlet {

    private final AcaoEducativaDAO dao = new AcaoEducativaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String blocoParam = req.getParameter("bloco");
                List<AcaoEducativa> lista = blocoParam != null
                    ? dao.listarPorBloco(UUID.fromString(blocoParam))
                    : dao.listarTodos();
                res.getWriter().print(gson.toJson(lista));
            } else {
                AcaoEducativa a = dao.buscarPorId(UUID.fromString(id));
                if (a != null) res.getWriter().print(gson.toJson(a));
                else erro(res, 404, "Ação educativa não encontrada");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            AcaoEducativa a = gson.fromJson(lerBody(req), AcaoEducativa.class);
            AcaoEducativa criada = dao.inserir(a);
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
            AcaoEducativa a = gson.fromJson(lerBody(req), AcaoEducativa.class);
            a.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(a);
            if (ok) ok(res, "Ação educativa atualizada com sucesso");
            else erro(res, 404, "Ação educativa não encontrada");
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
            if (ok) ok(res, "Ação educativa removida com sucesso");
            else erro(res, 404, "Ação educativa não encontrada");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
