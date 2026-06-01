package com.escoteiros.servlet;

import com.escoteiros.dao.AssociadoDistintivoDAO;
import com.escoteiros.model.AssociadoDistintivo;
import com.escoteiros.util.BaseServlet;
import com.escoteiros.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET    /api/associado-distintivos?associado={uuid}  → lista por lobinho (com dados do distintivo)
 *   GET    /api/associado-distintivos?alcateia={uuid}   → lista por alcateia
 *   POST   /api/associado-distintivos                   → atribui distintivo a um lobinho
 *   DELETE /api/associado-distintivos/{id}              → remove atribuição
 */
@WebServlet("/api/associado-distintivos/*")
public class AssociadoDistintivoServlet extends BaseServlet {

    private final AssociadoDistintivoDAO dao = new AssociadoDistintivoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String id = extrairId(req);
        try {
            if (id != null) {
                AssociadoDistintivo ad = dao.buscarPorId(UUID.fromString(id));
                if (ad != null) res.getWriter().print(gson.toJson(ad));
                else erro(res, 404, "Atribuição de distintivo não encontrada");
                return;
            }

            String associadoParam = req.getParameter("associado");
            String alcateiaParam  = req.getParameter("alcateia");

            if (associadoParam != null) {
                res.getWriter().print(gson.toJson(dao.listarPorAssociado(UUID.fromString(associadoParam))));
            } else if (alcateiaParam != null) {
                res.getWriter().print(gson.toJson(dao.listarPorAlcateia(UUID.fromString(alcateiaParam))));
            } else {
                erro(res, 400, "Parâmetro 'associado' ou 'alcateia' é obrigatório");
            }
        } catch (IllegalArgumentException e) {
            erro(res, 400, "UUID inválido: " + e.getMessage());
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            AssociadoDistintivo ad = gson.fromJson(lerBody(req), AssociadoDistintivo.class);

            if (ad.getAssociadoId() == null || ad.getDistintivoId() == null) {
                erro(res, 400, "associadoId e distintivoId são obrigatórios");
                return;
            }

            UUID atribuidoPor = extrairAssociadoIdDoJwt(req);
            if (atribuidoPor != null) ad.setAtribuidoPor(atribuidoPor);

            AssociadoDistintivo criado = dao.inserir(ad);
            res.setStatus(201);
            res.getWriter().print(gson.toJson(criado));
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
            if (dao.deletar(UUID.fromString(id))) ok(res, "Distintivo removido com sucesso");
            else erro(res, 404, "Atribuição de distintivo não encontrada");
        } catch (IllegalArgumentException e) {
            erro(res, 400, "UUID inválido: " + e.getMessage());
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }

    private UUID extrairAssociadoIdDoJwt(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        try {
            Claims claims = JwtUtil.validar(auth.substring(7).trim());
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }
}
