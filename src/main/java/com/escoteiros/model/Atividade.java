package com.escoteiros.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Atividade {
    private UUID           id;
    private UUID           acaoEducativaId;
    private UUID           criadoPor;
    private UUID           alcateiaId;
    private boolean        ePersonalizada;
    private String         nomePersonalizado;
    private String         descricaoPersonalizada;
    private UUID           aprovadaPor;
    private LocalDate      dataLimite;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public UUID getId()                             { return id; }
    public void setId(UUID id)                      { this.id = id; }
    public UUID getAcaoEducativaId()                { return acaoEducativaId; }
    public void setAcaoEducativaId(UUID a)          { this.acaoEducativaId = a; }
    public UUID getCriadoPor()                      { return criadoPor; }
    public void setCriadoPor(UUID c)                { this.criadoPor = c; }
    public UUID getAlcateiaId()                     { return alcateiaId; }
    public void setAlcateiaId(UUID a)               { this.alcateiaId = a; }
    public boolean isEPersonalizada()               { return ePersonalizada; }
    public void setEPersonalizada(boolean e)        { this.ePersonalizada = e; }
    public String getNomePersonalizado()            { return nomePersonalizado; }
    public void setNomePersonalizado(String n)      { this.nomePersonalizado = n; }
    public String getDescricaoPersonalizada()       { return descricaoPersonalizada; }
    public void setDescricaoPersonalizada(String d) { this.descricaoPersonalizada = d; }
    public UUID getAprovadaPor()                    { return aprovadaPor; }
    public void setAprovadaPor(UUID a)              { this.aprovadaPor = a; }
    public LocalDate getDataLimite()                { return dataLimite; }
    public void setDataLimite(LocalDate d)          { this.dataLimite = d; }
    public OffsetDateTime getCriadoEm()             { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)       { this.criadoEm = t; }
    public OffsetDateTime getAtualizadoEm()         { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)   { this.atualizadoEm = t; }
}
