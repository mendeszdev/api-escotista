package com.escoteiros.servlet;

import com.escoteiros.dao.AtribuicaoDetalhadaDAO;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints:
 *   GET /api/atribuicoes-detalhadas?alcateia={uuid}  → atribuições da alcateia com dados joinados
 *   GET /api/atribuicoes-detalhadas?associado={uuid} → atribuições do lobinho com dados joinados
 */
@WebServlet("/api/atribuicoes-detalhadas/*")
public class AtribuicaoDetalhadaServlet extends BaseServlet {

    private final AtribuicaoDetalhadaDAO dao = new AtribuicaoDetalhadaDAO();

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
                erro(res, 400, "Parâmetro 'alcateia' ou 'associado' é obrigatório");
            }
        } catch (IllegalArgumentException e) {
            erro(res, 400, "UUID inválido: " + e.getMessage());
        } catch (Exception e) {
            erro(res, 500, e.getMessage());
        }
    }
}
