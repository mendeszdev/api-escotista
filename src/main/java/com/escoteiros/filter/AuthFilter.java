package com.escoteiros.filter;

import com.escoteiros.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

@WebFilter("/api/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC = Set.of(
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/recuperar-senha"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Responde OPTIONS diretamente com CORS headers, sem depender do CorsFilter.
        // Garante que o preflight funcione independente da ordem dos filtros.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin",  "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (PUBLIC.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(response, "Token não informado");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (!JwtUtil.isValido(token)) {
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
