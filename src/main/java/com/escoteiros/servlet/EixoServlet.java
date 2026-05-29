package com.escoteiros.servlet;

import com.escoteiros.dao.EixoDAO;
import com.escoteiros.model.Eixo;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/eixos/*")
public class EixoServlet extends BaseServlet {

    private final EixoDAO dao = new EixoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                res.getWriter().print(gson.toJson(dao.listarTodos()));
            } else {
                Eixo e = dao.buscarPorId(UUID.fromString(id));
                if (e != null) res.getWriter().print(gson.toJson(e));
                else erro(res, 404, "Eixo não encontrado");
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Eixo e = gson.fromJson(lerBody(req), Eixo.class);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(dao.inserir(e)));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            Eixo e = gson.fromJson(lerBody(req), Eixo.class);
            e.setId(UUID.fromString(id));
            if (dao.atualizar(e)) ok(res, "Eixo atualizado");
            else erro(res, 404, "Eixo não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Eixo removido");
            else erro(res, 404, "Eixo não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
