package com.escoteiros.servlet;

import com.escoteiros.dao.FichaMedicaDAO;
import com.escoteiros.model.FichaMedica;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/fichas-medicas?associado={uuid}    → busca ficha do associado
 *   POST   /api/fichas-medicas                     → cria ou atualiza (upsert)
 */
@WebServlet("/api/fichas-medicas/*")
public class FichaMedicaServlet extends BaseServlet {

    private final FichaMedicaDAO dao = new FichaMedicaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String associadoParam = req.getParameter("associado");
        if (associadoParam == null) {
            erro(res, 400, "Informe ?associado={uuid}");
            return;
        }
        try {
            FichaMedica f = dao.buscarPorAssociado(UUID.fromString(associadoParam));
            if (f != null) res.getWriter().print(gson.toJson(f));
            else erro(res, 404, "Ficha médica não encontrada");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    // POST cria ou atualiza (upsert por associado_id)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            FichaMedica f = gson.fromJson(lerBody(req), FichaMedica.class);
            FichaMedica salva = dao.salvarOuAtualizar(f);
            res.setStatus(200); // upsert → sempre 200
            res.getWriter().print(gson.toJson(salva));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
