package com.escoteiros.filter;

import com.escoteiros.util.TokenStore;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

/**
 * Valida o Bearer token em todas as rotas /api/* exceto as públicas.
 * Deve rodar após o CorsFilter — declarar ordem em web.xml se necessário.
 */
@WebFilter("/api/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC = Set.of("/api/auth/login", "/api/auth/refresh");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        // OPTIONS já tratado pelo CorsFilter; rotas públicas passam direto
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || PUBLIC.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(response, "Token não informado");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (!TokenStore.isValid(token)) {
            unauthorized(response, "Token inválido ou expirado");
            return;
        }

        chain.doFilter(req, res);
    }

    private void unauthorized(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json;charset=UTF-8");
        // Repete os cabeçalhos CORS para que o cliente receba a resposta 401
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.getWriter().print("{\"erro\":\"" + msg + "\"}");
    }
}
