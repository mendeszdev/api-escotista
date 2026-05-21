package com.escoteiros.servlet;

import com.escoteiros.dao.ResponsavelLegalDAO;
import com.escoteiros.model.ResponsavelLegal;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/responsaveis                         → erro (exige filtro)
 *   GET    /api/responsaveis/{id}                    → busca por id
 *   GET    /api/responsaveis?associado={uuid}        → lista responsáveis do associado
 *   POST   /api/responsaveis                         → cria novo
 *   PUT    /api/responsaveis/{id}                    → atualiza
 *   DELETE /api/responsaveis/{id}                    → remove
 */
@WebServlet("/api/responsaveis/*")
public class ResponsavelLegalServlet extends BaseServlet {

    private final ResponsavelLegalDAO dao = new ResponsavelLegalDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String associadoParam = req.getParameter("associado");
                if (associadoParam == null) { erro(res, 400, "Informe ?associado={uuid}"); return; }
                res.getWriter().print(gson.toJson(dao.listarPorAssociado(UUID.fromString(associadoParam))));
            } else {
                ResponsavelLegal r = dao.buscarPorId(UUID.fromString(id));
                if (r != null) res.getWriter().print(gson.toJson(r));
                else erro(res, 404, "Responsável não encontrado");
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            ResponsavelLegal r = gson.fromJson(lerBody(req), ResponsavelLegal.class);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(dao.inserir(r)));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            ResponsavelLegal r = gson.fromJson(lerBody(req), ResponsavelLegal.class);
            r.setId(UUID.fromString(id));
            if (dao.atualizar(r)) ok(res, "Responsável atualizado");
            else erro(res, 404, "Responsável não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Responsável removido");
            else erro(res, 404, "Responsável não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
