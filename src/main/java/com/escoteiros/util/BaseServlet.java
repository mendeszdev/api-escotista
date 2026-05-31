package com.escoteiros.util;

import com.escoteiros.config.GsonConfig;
import com.google.gson.Gson;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.Map;

/**
 * Servlet base com helpers para:
 *   - preparar resposta JSON
 *   - ler body da requisição
 *   - extrair path params (/{id})
 *   - enviar erros padronizados
 */
public abstract class BaseServlet extends HttpServlet {

    protected final Gson gson = GsonConfig.get();

    /** Configura Content-Type da resposta */
    protected void jsonResponse(HttpServletResponse res) {
        res.setContentType("application/json;charset=UTF-8");
    }

    /** Lê o body da requisição como String */
    protected String lerBody(HttpServletRequest req) throws IOException {
        req.setCharacterEncoding("UTF-8");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Extrai o segmento após /api/recurso/ do path.
     * Ex: /api/associados/abc-123  →  "abc-123"
     * Retorna null se não houver segmento (rota de coleção).
     */
    protected String extrairId(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) return null;
        return path.substring(1);  // remove a barra inicial
    }

    /** Envia JSON de erro padronizado — usa Gson para escapar corretamente */
    protected void erro(HttpServletResponse res, int status, String mensagem)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().print(gson.toJson(Map.of("erro", mensagem != null ? mensagem : "Erro interno")));
    }

    /** Envia JSON de sucesso simples — usa Gson para escapar corretamente */
    protected void ok(HttpServletResponse res, String mensagem) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().print(gson.toJson(Map.of("mensagem", mensagem != null ? mensagem : "OK")));
    }
}
