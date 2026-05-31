package com.escoteiros.servlet;

import com.escoteiros.dao.AlcateiaDAO;
import com.escoteiros.model.Alcateia;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/alcateias                      → lista todas
 *   GET    /api/alcateias/{id}                 → busca por id
 *   GET    /api/alcateias?grupo={uuid}         → filtra por grupo escoteiro
 *   POST   /api/alcateias                      → cria nova
 *   PUT    /api/alcateias/{id}                 → atualiza
 *   DELETE /api/alcateias/{id}                 → remove
 */
@WebServlet("/api/alcateias/*")
public class AlcateiaServlet extends BaseServlet {

    private final AlcateiaDAO dao = new AlcateiaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String grupoParam = req.getParameter("grupo");
                UUID grupoId = grupoParam != null ? UUID.fromString(grupoParam) : null;
                res.getWriter().print(gson.toJson(dao.listarComStats(grupoId)));
            } else {
                Alcateia a = dao.buscarPorId(UUID.fromString(id));
                if (a != null) res.getWriter().print(gson.toJson(a));
                else erro(res, 404, "Alcateia não encontrada");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Alcateia a = gson.fromJson(lerBody(req), Alcateia.class);
            Alcateia criada = dao.inserir(a);
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
            Alcateia a = gson.fromJson(lerBody(req), Alcateia.class);
            a.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(a);
            if (ok) ok(res, "Alcateia atualizada com sucesso");
            else erro(res, 404, "Alcateia não encontrada");
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
            if (ok) ok(res, "Alcateia removida com sucesso");
            else erro(res, 404, "Alcateia não encontrada");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
