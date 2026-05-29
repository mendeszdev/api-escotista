package com.escoteiros.servlet;

import com.escoteiros.dao.MembroMatilhaDAO;
import com.escoteiros.model.MembroMatilha;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/membros-matilha/*")
public class MembroMatilhaServlet extends BaseServlet {

    private final MembroMatilhaDAO dao = new MembroMatilhaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            String matilhaParam = req.getParameter("matilha");
            if (matilhaParam == null) { erro(res, 400, "Informe ?matilha={uuid}"); return; }
            res.getWriter().print(gson.toJson(dao.listarPorMatilha(UUID.fromString(matilhaParam))));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            MembroMatilha m = gson.fromJson(lerBody(req), MembroMatilha.class);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(dao.inserir(m)));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.encerrar(UUID.fromString(id))) ok(res, "Participação encerrada");
            else erro(res, 404, "Membro não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Membro removido");
            else erro(res, 404, "Membro não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
