package com.escoteiros.servlet;

import com.escoteiros.dao.ProgressaoDAO;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET  /api/progressoes                         → lista todas
 *   GET  /api/progressoes?associado={uuid}        → progressão por associado
 *
 *  Progressões são atualizadas automaticamente pelo trigger fn_atualizar_progressao
 *  no banco — não é necessário POST/PUT manual.
 */
@WebServlet("/api/progressoes/*")
public class ProgressaoServlet extends BaseServlet {

    private final ProgressaoDAO dao = new ProgressaoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            String associadoParam = req.getParameter("associado");
            if (associadoParam != null) {
                res.getWriter().print(gson.toJson(dao.listarPorAssociado(UUID.fromString(associadoParam))));
            } else {
                res.getWriter().print(gson.toJson(dao.listarTodos()));
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
