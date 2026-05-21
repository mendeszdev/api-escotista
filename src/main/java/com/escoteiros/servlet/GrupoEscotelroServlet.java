package com.escoteiros.servlet;

import com.escoteiros.dao.GrupoEscotelroDAO;
import com.escoteiros.model.GrupoEscoteiro;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/grupos-escoteiros          → lista todos
 *   GET    /api/grupos-escoteiros/{id}     → busca por id
 *   POST   /api/grupos-escoteiros          → cria novo
 *   PUT    /api/grupos-escoteiros/{id}     → atualiza
 *   DELETE /api/grupos-escoteiros/{id}     → remove
 */
@WebServlet("/api/grupos-escoteiros/*")
public class GrupoEscotelroServlet extends BaseServlet {

    private final GrupoEscotelroDAO dao = new GrupoEscotelroDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                List<GrupoEscoteiro> lista = dao.listarTodos();
                res.getWriter().print(gson.toJson(lista));
            } else {
                GrupoEscoteiro g = dao.buscarPorId(UUID.fromString(id));
                if (g != null) res.getWriter().print(gson.toJson(g));
                else erro(res, 404, "Grupo escoteiro não encontrado");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            GrupoEscoteiro g = gson.fromJson(lerBody(req), GrupoEscoteiro.class);
            GrupoEscoteiro criado = dao.inserir(g);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(criado));
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
            GrupoEscoteiro g = gson.fromJson(lerBody(req), GrupoEscoteiro.class);
            g.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(g);
            if (ok) ok(res, "Atualizado com sucesso");
            else erro(res, 404, "Grupo escoteiro não encontrado");
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
            if (ok) ok(res, "Removido com sucesso");
            else erro(res, 404, "Grupo escoteiro não encontrado");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
