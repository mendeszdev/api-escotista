package com.escoteiros.servlet;

import com.escoteiros.dao.MatilhaDAO;
import com.escoteiros.model.Matilha;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/matilhas                        → lista todas
 *   GET    /api/matilhas/{id}                   → busca por id
 *   GET    /api/matilhas?alcateia={uuid}        → filtra por alcateia
 *   POST   /api/matilhas                        → cria nova
 *   PUT    /api/matilhas/{id}                   → atualiza
 *   DELETE /api/matilhas/{id}                   → remove
 */
@WebServlet("/api/matilhas/*")
public class MatilhaServlet extends BaseServlet {

    private final MatilhaDAO dao = new MatilhaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String alcateiaParam = req.getParameter("alcateia");
                List<Matilha> lista = alcateiaParam != null
                    ? dao.listarPorAlcateia(UUID.fromString(alcateiaParam))
                    : dao.listarTodos();
                res.getWriter().print(gson.toJson(lista));
            } else {
                Matilha m = dao.buscarPorId(UUID.fromString(id));
                if (m != null) res.getWriter().print(gson.toJson(m));
                else erro(res, 404, "Matilha não encontrada");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Matilha m = gson.fromJson(lerBody(req), Matilha.class);
            Matilha criada = dao.inserir(m);
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
            Matilha m = gson.fromJson(lerBody(req), Matilha.class);
            m.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(m);
            if (ok) ok(res, "Matilha atualizada com sucesso");
            else erro(res, 404, "Matilha não encontrada");
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
            if (ok) ok(res, "Matilha removida com sucesso");
            else erro(res, 404, "Matilha não encontrada");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
