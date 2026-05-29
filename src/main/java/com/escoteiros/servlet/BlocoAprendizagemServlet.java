package com.escoteiros.servlet;

import com.escoteiros.dao.BlocoAprendizagemDAO;
import com.escoteiros.model.BlocoAprendizagem;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/blocos-aprendizagem/*")
public class BlocoAprendizagemServlet extends BaseServlet {

    private final BlocoAprendizagemDAO dao = new BlocoAprendizagemDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String eixoParam = req.getParameter("eixo");
                if (eixoParam != null) {
                    res.getWriter().print(gson.toJson(dao.listarPorEixo(UUID.fromString(eixoParam))));
                } else {
                    res.getWriter().print(gson.toJson(dao.listarTodos()));
                }
            } else {
                BlocoAprendizagem b = dao.buscarPorId(UUID.fromString(id));
                if (b != null) res.getWriter().print(gson.toJson(b));
                else erro(res, 404, "Bloco não encontrado");
            }
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            BlocoAprendizagem b = gson.fromJson(lerBody(req), BlocoAprendizagem.class);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(dao.inserir(b)));
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            BlocoAprendizagem b = gson.fromJson(lerBody(req), BlocoAprendizagem.class);
            b.setId(UUID.fromString(id));
            if (dao.atualizar(b)) ok(res, "Bloco atualizado");
            else erro(res, 404, "Bloco não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        if (id == null) { erro(res, 400, "ID obrigatório"); return; }
        try {
            if (dao.deletar(UUID.fromString(id))) ok(res, "Bloco removido");
            else erro(res, 404, "Bloco não encontrado");
        } catch (Exception e) { erro(res, 500, e.getMessage()); }
    }
}
