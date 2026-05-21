package com.escoteiros.servlet;

import com.escoteiros.dao.AtividadeDAO;
import com.escoteiros.model.Atividade;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/atividades                        → lista todas
 *   GET    /api/atividades/{id}                   → busca por id
 *   GET    /api/atividades?alcateia={uuid}        → filtra por alcateia
 *   POST   /api/atividades                        → cria nova
 *   PUT    /api/atividades/{id}                   → atualiza
 *   DELETE /api/atividades/{id}                   → remove
 */
@WebServlet("/api/atividades/*")
public class AtividadeServlet extends BaseServlet {

    private final AtividadeDAO dao = new AtividadeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String alcateiaParam = req.getParameter("alcateia");
                List<Atividade> lista = alcateiaParam != null
                    ? dao.listarPorAlcateia(UUID.fromString(alcateiaParam))
                    : dao.listarTodos();
                res.getWriter().print(gson.toJson(lista));
            } else {
                Atividade a = dao.buscarPorId(UUID.fromString(id));
                if (a != null) res.getWriter().print(gson.toJson(a));
                else erro(res, 404, "Atividade não encontrada");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Atividade a = gson.fromJson(lerBody(req), Atividade.class);
            Atividade criada = dao.inserir(a);
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
            Atividade a = gson.fromJson(lerBody(req), Atividade.class);
            a.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(a);
            if (ok) ok(res, "Atividade atualizada com sucesso");
            else erro(res, 404, "Atividade não encontrada");
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
            if (ok) ok(res, "Atividade removida com sucesso");
            else erro(res, 404, "Atividade não encontrada");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
