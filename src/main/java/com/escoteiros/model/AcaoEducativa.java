package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AcaoEducativa {
    private UUID           id;
    private UUID           blocoId;
    private String         nome;
    private String         descricao;
    private String         tipo;         // enum acao_tipo
    private boolean        eEspecialidade;
    private UUID           criadoPor;
    private boolean        ativo;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public UUID getId()                            { return id; }
    public void setId(UUID id)                     { this.id = id; }
    public UUID getBlocoId()                       { return blocoId; }
    public void setBlocoId(UUID b)                 { this.blocoId = b; }
    public String getNome()                        { return nome; }
    public void setNome(String n)                  { this.nome = n; }
    public String getDescricao()                   { return descricao; }
    public void setDescricao(String d)             { this.descricao = d; }
    public String getTipo()                        { return tipo; }
    public void setTipo(String t)                  { this.tipo = t; }
    public boolean isEEspecialidade()              { return eEspecialidade; }
    public void setEEspecialidade(boolean e)       { this.eEspecialidade = e; }
    public UUID getCriadoPor()                     { return criadoPor; }
    public void setCriadoPor(UUID c)               { this.criadoPor = c; }
    public boolean isAtivo()                       { return ativo; }
    public void setAtivo(boolean a)                { this.ativo = a; }
    public OffsetDateTime getCriadoEm()            { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)      { this.criadoEm = t; }
    public OffsetDateTime getAtualizadoEm()        { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)  { this.atualizadoEm = t; }
}
