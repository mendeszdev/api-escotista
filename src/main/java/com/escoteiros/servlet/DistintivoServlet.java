package com.escoteiros.servlet;

import com.escoteiros.dao.DistintivoDAO;
import com.escoteiros.model.Distintivo;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/distintivos          → lista todos
 *   GET    /api/distintivos/{id}     → busca por id
 *   POST   /api/distintivos          → cria novo
 *   PUT    /api/distintivos/{id}     → atualiza
 *   DELETE /api/distintivos/{id}     → remove
 */
@WebServlet("/api/distintivos/*")
public class DistintivoServlet extends BaseServlet {

    private final DistintivoDAO dao = new DistintivoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                res.getWriter().print(gson.toJson(dao.listarTodos()));
            } else {
                Distintivo d = dao.buscarPorId(UUID.fromString(id));
                if (d != null) res.getWriter().print(gson.toJson(d));
                else erro(res, 404, "Distintivo não encontrado");
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Distintivo d = gson.fromJson(lerBody(req), Distintivo.class);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(dao.inserir(d)));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            Distintivo d = gson.fromJson(lerBody(req), Distintivo.class);
            d.setId(UUID.fromString(id));
            if (dao.atualizar(d)) ok(res, "Distintivo atualizado");
            else erro(res, 404, "Distintivo não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Distintivo removido");
            else erro(res, 404, "Distintivo não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
