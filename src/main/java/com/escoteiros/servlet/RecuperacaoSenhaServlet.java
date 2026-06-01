package com.escoteiros.servlet;

import com.escoteiros.dao.AssociadoDAO;
import com.escoteiros.util.BaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

/**
 * POST /api/auth/recuperar-senha
 * Body: { "matricula": "000001", "cpf": "000.000.000-00", "novaSenha": "minhasenha" }
 *
 * Fluxo:
 *   1. Verifica se matrícula + CPF correspondem a um associado ativo
 *   2. Se sim, atualiza a senha com BCrypt
 *   3. Retorna sucesso ou erro
 *
 * Rota pública — não requer JWT (declarada no AuthFilter)
 */
@WebServlet("/api/auth/recuperar-senha")
public class RecuperacaoSenhaServlet extends BaseServlet {

    private final AssociadoDAO dao = new AssociadoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Map<?, ?> body = gson.fromJson(lerBody(req), Map.class);
            if (body == null) { erro(res, 400, "Body inválido"); return; }

            String matricula  = (String) body.get("matricula");
            String cpf        = (String) body.get("cpf");
            String novaSenha  = (String) body.get("novaSenha");

            if (isBlank(matricula) || isBlank(cpf) || isBlank(novaSenha)) {
                erro(res, 400, "matricula, cpf e novaSenha são obrigatórios");
                return;
            }

            if (novaSenha.length() < 6) {
                erro(res, 400, "A senha deve ter no mínimo 6 caracteres");
                return;
            }

            boolean ok = dao.redefinirSenha(matricula.trim(), cpf.trim(), novaSenha);
            if (ok) {
                res.getWriter().print(gson.toJson(Map.of("sucesso", true, "mensagem", "Senha redefinida com sucesso")));
            } else {
                erro(res, 401, "Matrícula ou CPF incorretos");
            }

        } catch (Exception e) {
            erro(res, 500, "Erro interno: " + e.getMessage());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
