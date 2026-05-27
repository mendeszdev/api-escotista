package com.escoteiros.servlet;

import com.escoteiros.util.BaseServlet;
import com.escoteiros.util.TokenStore;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/auth/logout")
public class LogoutServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        jsonResponse(res);
        String token = req.getHeader("Authorization").substring(7).trim();
        TokenStore.revoke(token);
        ok(res, "Logout realizado com sucesso");
    }
}
