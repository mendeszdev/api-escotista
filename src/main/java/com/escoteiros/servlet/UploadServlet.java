package com.escoteiros.servlet;

import com.escoteiros.config.GsonConfig;
import com.escoteiros.dao.ArquivoDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * POST /api/upload
 *
 * Recebe multipart/form-data com o campo "file" e salva os bytes na tabela
 * `arquivos` do Neon. Retorna JSON com a URL pública para servir o arquivo.
 *
 * A URL de serviço é: GET /api/files/{id}
 */
@WebServlet("/api/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,       // flush p/ disco após 1 MB em memória
    maxFileSize       = 15 * 1024 * 1024,  // máximo 15 MB por arquivo
    maxRequestSize    = 20 * 1024 * 1024   // máximo 20 MB por requisição
)
public class UploadServlet extends HttpServlet {

    private final ArquivoDAO dao  = new ArquivoDAO();
    private final Gson       gson = GsonConfig.get();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            res.setStatus(400);
            res.getWriter().print(gson.toJson(Map.of("erro", "Campo 'file' ausente ou vazio")));
            return;
        }

        // Nome seguro: pega apenas o nome do arquivo, descarta caminhos
        String submitted = filePart.getSubmittedFileName();
        String nome = (submitted != null)
            ? Paths.get(submitted).getFileName().toString()
            : "arquivo";

        String tipoMime = filePart.getContentType() != null
            ? filePart.getContentType()
            : "application/octet-stream";

        byte[] dados;
        try {
            dados = filePart.getInputStream().readAllBytes();
        } catch (IOException e) {
            res.setStatus(500);
            res.getWriter().print(gson.toJson(Map.of("erro", "Erro ao ler arquivo: " + e.getMessage())));
            return;
        }

        UUID id;
        try {
            id = dao.inserir(nome, tipoMime, dados);
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().print(gson.toJson(Map.of("erro", "Erro ao salvar no banco: " + e.getMessage())));
            return;
        }

        String fileUrl = construirBaseUrl(req) + "/api/files/" + id;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",           id.toString());
        result.put("url",          fileUrl);
        result.put("nome",         nome);
        result.put("tipoMime",     tipoMime);
        result.put("tamanhoBytes", (long) dados.length);

        res.setStatus(201);
        res.getWriter().print(gson.toJson(result));
    }

    /** Constrói a URL base respeitando proxies reversos (Render.com, etc.) */
    private String construirBaseUrl(HttpServletRequest req) {
        String proto = req.getHeader("X-Forwarded-Proto");
        if (proto == null || proto.isBlank()) proto = req.getScheme();

        String host = req.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = req.getServerName();
            int port = req.getServerPort();
            if (port != 80 && port != 443) host += ":" + port;
        }

        String ctx = req.getContextPath();
        return proto + "://" + host + (ctx != null ? ctx : "");
    }
}
