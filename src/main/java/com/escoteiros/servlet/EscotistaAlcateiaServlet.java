package com.escoteiros.servlet;

import com.escoteiros.dao.EscotistaAlcateiaDAO;
import com.escoteiros.model.EscotistaAlcateia;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/escotistas-alcateias/*")
public class EscotistaAlcateiaServlet extends BaseServlet {

    private final EscotistaAlcateiaDAO dao = new EscotistaAlcateiaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            String alcateiaParam  = req.getParameter("alcateia");
            String associadoParam = req.getParameter("associado");
            if (alcateiaParam != null) {
                res.getWriter().print(gson.toJson(dao.listarPorAlcateia(UUID.fromString(alcateiaParam))));
            } else if (associadoParam != null) {
                res.getWriter().print(gson.toJson(dao.listarPorAssociado(UUID.fromString(associadoParam))));
            } else {
                erro(res, 400, "Informe ?alcateia={uuid} ou ?associado={uuid}");
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            EscotistaAlcateia e = gson.fromJson(lerBody(req), EscotistaAlcateia.class);
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
            if (dao.encerrar(UUID.fromString(id))) ok(res, "Vínculo encerrado");
            else erro(res, 404, "Vínculo não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Vínculo removido");
            else erro(res, 404, "Vínculo não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
