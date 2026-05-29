package com.escoteiros.servlet;

import com.escoteiros.config.JwtConfig;
import com.escoteiros.dao.AssociadoDAO;
import com.escoteiros.model.Associado;
import com.escoteiros.util.BaseServlet;
import com.escoteiros.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * POST /api/auth/refresh
 * Body: { "refreshToken": "<jwt>" }
 *
 * Valida o refresh token JWT e emite um novo par de tokens.
 *
 * Resposta de sucesso:
 * {
 *   "accessToken":  "<novo jwt>",
 *   "refreshToken": "<novo jwt>",
 *   "expiresIn":    1800
 * }
 */
@WebServlet("/api/auth/refresh")
public class RefreshServlet extends BaseServlet {

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

            String refreshToken = (String) body.get("refreshToken");
            if (refreshToken == null || refreshToken.isBlank()) {
                erro(res, 400, "refreshToken é obrigatório");
                return;
            }

            // Valida assinatura, expiração e tipo do token
            Claims claims;
            try {
                claims = JwtUtil.validar(refreshToken);
            } catch (JwtException | IllegalArgumentException e) {
                res.setStatus(401);
                res.getWriter().print(gson.toJson(Map.of(
                    "success", false,
                    "message", "Refresh token inválido ou expirado"
                )));
                return;
            }

            if (!"refresh".equals(claims.get("type", String.class))) {
                res.setStatus(401);
                res.getWriter().print(gson.toJson(Map.of(
                    "success", false,
                    "message", "Tipo de token inválido"
                )));
                return;
            }

            UUID associadoId = UUID.fromString(claims.getSubject());
            Associado associado = associadoDAO.buscarPorId(associadoId);

            if (associado == null || !"ativo".equals(associado.getStatus())) {
                res.setStatus(401);
                res.getWriter().print(gson.toJson(Map.of(
                    "success", false,
                    "message", "Usuário inativo ou não encontrado"
                )));
                return;
            }

            String role            = mapearPerfil(associado.getPerfil());
            String novoAccessToken = JwtUtil.gerarAccessToken(
                associado.getId(), associado.getMatricula(), role,
                associado.getNomeCompleto(), associado.getGrupoEscoteiroId());
            String novoRefreshToken = JwtUtil.gerarRefreshToken(associado.getId());

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("accessToken",  novoAccessToken);
            resposta.put("refreshToken", novoRefreshToken);
            resposta.put("expiresIn",    JwtConfig.ACCESS_EXPIRY_MINUTES * 60);

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
