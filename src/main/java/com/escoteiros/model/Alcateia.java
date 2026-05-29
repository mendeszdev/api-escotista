package com.escoteiros.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Alcateia {
    private UUID           id;
    private UUID           grupoEscoteiroId;
    private String         nome;
    private String         descricao;
    private String         status;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public UUID getId()                            { return id; }
    public void setId(UUID id)                     { this.id = id; }
    public UUID getGrupoEscoteiroId()              { return grupoEscoteiroId; }
    public void setGrupoEscoteiroId(UUID g)        { this.grupoEscoteiroId = g; }
    public String getNome()                        { return nome; }
    public void setNome(String n)                  { this.nome = n; }
    public String getDescricao()                   { return descricao; }
    public void setDescricao(String d)             { this.descricao = d; }
    public String getStatus()                      { return status; }
    public void setStatus(String s)                { this.status = s; }
    public OffsetDateTime getCriadoEm()            { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)      { this.criadoEm = t; }
    public OffsetDateTime getAtualizadoEm()        { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)  { this.atualizadoEm = t; }
}
