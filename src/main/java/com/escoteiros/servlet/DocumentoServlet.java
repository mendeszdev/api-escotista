package com.escoteiros.servlet;

import com.escoteiros.dao.DocumentoDAO;
import com.escoteiros.model.Documento;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/documentos                          → erro (exige filtro)
 *   GET    /api/documentos/{id}                     → busca por id
 *   GET    /api/documentos?associado={uuid}         → lista por associado
 *   POST   /api/documentos                          → cria novo
 *   DELETE /api/documentos/{id}                     → remove
 */
@WebServlet("/api/documentos/*")
public class DocumentoServlet extends BaseServlet {

    private final DocumentoDAO dao = new DocumentoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String associadoParam = req.getParameter("associado");
                if (associadoParam == null) {
                    erro(res, 400, "Informe ?associado={uuid}");
                    return;
                }
                res.getWriter().print(gson.toJson(dao.listarPorAssociado(UUID.fromString(associadoParam))));
            } else {
                Documento d = dao.buscarPorId(UUID.fromString(id));
                if (d != null) res.getWriter().print(gson.toJson(d));
                else erro(res, 404, "Documento não encontrado");
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Documento d = gson.fromJson(lerBody(req), Documento.class);
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
            Documento d = gson.fromJson(lerBody(req), Documento.class);
            d.setId(UUID.fromString(id));
            if (dao.atualizar(d)) ok(res, "Documento atualizado");
            else erro(res, 404, "Documento não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Documento removido");
            else erro(res, 404, "Documento não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
