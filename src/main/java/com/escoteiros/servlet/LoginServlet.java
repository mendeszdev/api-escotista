package com.escoteiros.servlet;

import com.escoteiros.config.JwtConfig;
import com.escoteiros.dao.AssociadoDAO;
import com.escoteiros.model.Associado;
import com.escoteiros.util.BaseServlet;
import com.escoteiros.util.JwtUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * POST /api/auth/login
 * Body: { "matricula": "123.456-7", "senha": "minhasenha" }
 *
 * Resposta de sucesso:
 * {
 *   "success":      true,
 *   "accessToken":  "<jwt — expira em ACCESS_EXPIRY_MINUTES>",
 *   "refreshToken": "<jwt — expira em REFRESH_EXPIRY_DAYS>",
 *   "expiresIn":    1800,
 *   "user": { id, name, nomeEscoteiro, matricula, role, email, fotoUrl, grupoId }
 * }
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends BaseServlet {

    private final AssociadoDAO associadoDAO = new AssociadoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        try {
            Map<?, ?> body = gson.fromJson(lerBody(req), Map.class);
            if (body == null) {
                erro(res, 400, "Body inválido");
                return;
            }

            String matricula = (String) body.get("matricula");
            String senha     = (String) body.get("senha");

            if (matricula == null || matricula.isBlank() ||
                senha     == null || senha.isBlank()) {
                erro(res, 400, "Matrícula e senha são obrigatórios");
                return;
            }

            Associado associado = associadoDAO.autenticar(matricula, senha);
            if (associado == null) {
                res.setStatus(401);
                res.getWriter().print(gson.toJson(Map.of(
                    "success", false,
                    "message", "Matrícula ou senha incorretos"
                )));
                return;
            }

            String role         = mapearPerfil(associado.getPerfil());
            String accessToken  = JwtUtil.gerarAccessToken(
                associado.getId(), associado.getMatricula(), role,
                associado.getNomeCompleto(), associado.getGrupoEscotelroId());
            String refreshToken = JwtUtil.gerarRefreshToken(associado.getId());

            Map<String, Object> user = new HashMap<>();
            user.put("id",           associado.getId().toString());
            user.put("name",         associado.getNomeCompleto());
            user.put("nomeEscoteiro",associado.getNomeEscoteiro());
            user.put("matricula",    associado.getMatricula());
            user.put("role",         role);
            user.put("email",        associado.getEmail());
            user.put("fotoUrl",      associado.getFotoUrl());
            user.put("grupoId",      associado.getGrupoEscotelroId() != null
                                        ? associado.getGrupoEscotelroId().toString() : null);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("success",      true);
            resposta.put("accessToken",  accessToken);
            resposta.put("refreshToken", refreshToken);
            resposta.put("expiresIn",    JwtConfig.ACCESS_EXPIRY_MINUTES * 60);
            resposta.put("user",         user);

            res.getWriter().print(gson.toJson(resposta));

        } catch (Exception e) {
            erro(res, 500, "Erro interno: " + e.getMessage());
        }
    }

    private String mapearPerfil(String perfil) {
        if (perfil == null) return "lobinho";
        return switch (perfil.toLowerCase()) {
            case "dirigente" -> "dirigente";
            case "escotista" -> "escotista";
            default          -> "lobinho";
        };
    }
}
