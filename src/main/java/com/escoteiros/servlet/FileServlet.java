package com.escoteiros.servlet;

import com.escoteiros.dao.ArquivoDAO;
import com.escoteiros.model.Arquivo;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

/**
 * GET /api/files/{uuid}
 *
 * Serve o arquivo binário armazenado na tabela `arquivos` do Neon.
 * Esse endpoint é público (sem JWT) para que img src= e links de download
 * funcionem diretamente no navegador.
 *
 * AuthFilter já está configurado para liberar GET /api/files/*.
 */
@WebServlet("/api/files/*")
public class FileServlet extends HttpServlet {

    private final ArquivoDAO dao = new ArquivoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setHeader("Access-Control-Allow-Origin", "*");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 2) {
            res.setStatus(400);
            res.getWriter().print("{\"erro\":\"ID do arquivo não informado\"}");
            return;
        }

        UUID id;
        try {
            id = UUID.fromString(pathInfo.substring(1));
        } catch (IllegalArgumentException e) {
            res.setStatus(400);
            res.getWriter().print("{\"erro\":\"UUID inválido\"}");
            return;
        }

        Arquivo arquivo;
        try {
            arquivo = dao.buscarPorId(id);
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().print("{\"erro\":\"Erro no banco: " + e.getMessage() + "\"}");
            return;
        }

        if (arquivo == null) {
            res.setStatus(404);
            res.getWriter().print("{\"erro\":\"Arquivo não encontrado\"}");
            return;
        }

        String mime = arquivo.getTipoMime() != null ? arquivo.getTipoMime() : "application/octet-stream";
        res.setContentType(mime);
        res.setContentLengthLong(arquivo.getTamanhoBytes() != null ? arquivo.getTamanhoBytes() : arquivo.getDados().length);

        // inline → abre no navegador; attachment → força download
        boolean isImagem = mime.startsWith("image/");
        String disposition = isImagem ? "inline" : "attachment";
        res.setHeader("Content-Disposition", disposition + "; filename=\"" + arquivo.getNome() + "\"");

        // Cache público de 1 hora para arquivos estáticos
        res.setHeader("Cache-Control", "public, max-age=3600");

        res.getOutputStream().write(arquivo.getDados());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 2) {
            res.setStatus(400);
            res.getWriter().print("{\"erro\":\"ID obrigatório\"}");
            return;
        }

        UUID id;
        try {
            id = UUID.fromString(pathInfo.substring(1));
        } catch (IllegalArgumentException e) {
            res.setStatus(400);
            res.getWriter().print("{\"erro\":\"UUID inválido\"}");
            return;
        }

        try {
            boolean removido = dao.deletar(id);
            if (removido) {
                res.getWriter().print("{\"mensagem\":\"Arquivo removido\"}");
            } else {
                res.setStatus(404);
                res.getWriter().print("{\"erro\":\"Arquivo não encontrado\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().print("{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }
}
