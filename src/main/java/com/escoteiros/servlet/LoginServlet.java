package com.escoteiros.servlet;

import com.escoteiros.dao.AssociadoDAO;
import com.escoteiros.model.Associado;
import com.escoteiros.util.BaseServlet;
import com.escoteiros.util.TokenStore;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoint de autenticação.
 *
 * POST /api/auth/login
 * Body: { "matricula": "123.456-7", "senha": "minhasenha" }
 *
 * Resposta de sucesso:
 * {
 *   "success": true,
 *   "token": "<uuid>",
 *   "user": {
 *     "id": "<uuid>",
 *     "name": "Nome Completo",
 *     "nomeEscoteiro": "Nome Escoteiro",
 *     "matricula": "123.456-7",
 *     "role": "lobinho",        ← mapeado do campo `perfil`
 *     "email": "...",
 *     "fotoUrl": "..."
 *   }
 * }
 *
 * ATENÇÃO: O token gerado aqui é apenas um UUID simples (sem expiração).
 * Em produção, substitua por JWT com biblioteca como `java-jwt` ou `jjwt`.
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends BaseServlet {

    private final AssociadoDAO dao = new AssociadoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);

        try {
            // 1. Ler body da requisição
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

            // 2. Buscar associado pela matrícula + validar senha
            Associado associado = dao.autenticar(matricula, senha);

            if (associado == null) {
                res.setStatus(401);
                res.getWriter().print(gson.toJson(Map.of(
                    "success", false,
                    "message", "Matrícula ou senha incorretos"
                )));
                return;
            }

            // 3. Gerar token e registrar com expiração de 8 h
            String token = TokenStore.register(UUID.randomUUID().toString());

            // 4. Montar objeto de usuário para o frontend
            // Campo `perfil` do banco → `role` esperado pelo frontend
            Map<String, Object> user = new HashMap<>();
            user.put("id",           associado.getId().toString());
            user.put("name",         associado.getNomeCompleto());
            user.put("nomeEscoteiro",associado.getNomeEscoteiro());
            user.put("matricula",    associado.getMatricula());
            user.put("role",         mapearPerfil(associado.getPerfil())); // perfil_tipo → role
            user.put("email",        associado.getEmail());
            user.put("fotoUrl",      associado.getFotoUrl());
            user.put("grupoId",      associado.getGrupoEscotelroId() != null
                                        ? associado.getGrupoEscotelroId().toString() : null);

            // 5. Retornar resposta
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("success", true);
            resposta.put("token",   token);
            resposta.put("user",    user);

            res.getWriter().print(gson.toJson(resposta));

        } catch (Exception e) {
            erro(res, 500, "Erro interno: " + e.getMessage());
        }
    }

    /**
     * Mapeia o enum `perfil_tipo` do banco para o `role` esperado pelo frontend.
     * Ajuste os valores conforme os enums cadastrados no PostgreSQL.
     *
     * Valores comuns no banco   →  role no frontend
     * "lobinho"                 →  "lobinho"
     * "escotista"               →  "escotista"
     * "dirigente"               →  "dirigente"
     * "senior"                  →  "escotista"  (fallback)
     * "pioneiro"                →  "escotista"  (fallback)
     */
    private String mapearPerfil(String perfil) {
        if (perfil == null) return "lobinho";
        return switch (perfil.toLowerCase()) {
            case "dirigente" -> "dirigente";
            case "escotista" -> "escotista";
            default          -> "lobinho";
        };
    }
}
