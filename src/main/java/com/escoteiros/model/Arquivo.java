package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Arquivo {
    private UUID           id;
    private String         nome;
    private String         tipoMime;
    private byte[]         dados;       // não serializado no JSON de resposta
    private Long           tamanhoBytes;
    private OffsetDateTime criadoEm;

    public UUID getId()                           { return id; }
    public void setId(UUID id)                    { this.id = id; }
    public String getNome()                       { return nome; }
    public void setNome(String n)                 { this.nome = n; }
    public String getTipoMime()                   { return tipoMime; }
    public void setTipoMime(String t)             { this.tipoMime = t; }
    public byte[] getDados()                      { return dados; }
    public void setDados(byte[] d)                { this.dados = d; }
    public Long getTamanhoBytes()                 { return tamanhoBytes; }
    public void setTamanhoBytes(Long t)           { this.tamanhoBytes = t; }
    public OffsetDateTime getCriadoEm()           { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)     { this.criadoEm = t; }
}
