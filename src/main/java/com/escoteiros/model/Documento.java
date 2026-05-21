package com.escoteiros.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Documento {
    private UUID           id;
    private UUID           associadoId;
    private String         tipo;
    private String         nomeArquivo;
    private String         url;
    private Long           tamanhoBytes;
    private LocalDate      validade;
    private boolean        obrigatorio;
    private String         status;
    private OffsetDateTime criadoEm;

    public UUID getId()                        { return id; }
    public void setId(UUID id)                 { this.id = id; }
    public UUID getAssociadoId()               { return associadoId; }
    public void setAssociadoId(UUID a)         { this.associadoId = a; }
    public String getTipo()                    { return tipo; }
    public void setTipo(String t)              { this.tipo = t; }
    public String getNomeArquivo()             { return nomeArquivo; }
    public void setNomeArquivo(String n)       { this.nomeArquivo = n; }
    public String getUrl()                     { return url; }
    public void setUrl(String u)               { this.url = u; }
    public Long getTamanhoBytes()              { return tamanhoBytes; }
    public void setTamanhoBytes(Long t)        { this.tamanhoBytes = t; }
    public LocalDate getValidade()             { return validade; }
    public void setValidade(LocalDate v)       { this.validade = v; }
    public boolean isObrigatorio()             { return obrigatorio; }
    public void setObrigatorio(boolean o)      { this.obrigatorio = o; }
    public String getStatus()                  { return status; }
    public void setStatus(String s)            { this.status = s; }
    public OffsetDateTime getCriadoEm()        { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)  { this.criadoEm = t; }
}
