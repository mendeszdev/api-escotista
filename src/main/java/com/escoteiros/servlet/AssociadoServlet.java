package com.escoteiros.servlet;

import com.escoteiros.dao.AssociadoDAO;
import com.escoteiros.model.Associado;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/associados                         → lista todos
 *   GET    /api/associados/{id}                    → busca por id
 *   GET    /api/associados?grupo={uuid}            → filtra por grupo
 *   GET    /api/associados?perfil={perfil_tipo}    → filtra por perfil
 *   POST   /api/associados                         → cria novo
 *   PUT    /api/associados/{id}                    → atualiza
 *   DELETE /api/associados/{id}                    → remove
 */
@WebServlet("/api/associados/*")
public class AssociadoServlet extends BaseServlet {

    private final AssociadoDAO dao = new AssociadoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id == null) {
                String grupoParam  = req.getParameter("grupo");
                String perfilParam = req.getParameter("perfil");

                List<Associado> lista;
                if (grupoParam != null) {
                    lista = dao.listarPorGrupo(UUID.fromString(grupoParam));
                } else if (perfilParam != null) {
                    lista = dao.listarPorPerfil(perfilParam);
                } else {
                    lista = dao.listarTodos();
                }
                res.getWriter().print(gson.toJson(lista));
            } else {
                Associado a = dao.buscarPorId(UUID.fromString(id));
                if (a != null) res.getWriter().print(gson.toJson(a));
                else erro(res, 404, "Associado não encontrado");
            }
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Associado a = gson.fromJson(lerBody(req), Associado.class);
            // ATENÇÃO: Em produção, aplique BCrypt na senha antes de persistir
            // a.setSenhaHash(BCrypt.hashpw(senhaRaw, BCrypt.gensalt()));
            Associado criado = dao.inserir(a);
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
            Associado a = gson.fromJson(lerBody(req), Associado.class);
            a.setId(UUID.fromString(id));
            boolean ok = dao.atualizar(a);
            if (ok) ok(res, "Associado atualizado com sucesso");
            else erro(res, 404, "Associado não encontrado");
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
            if (ok) ok(res, "Associado removido com sucesso");
            else erro(res, 404, "Associado não encontrado");
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
