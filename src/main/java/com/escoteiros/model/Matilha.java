package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Matilha {
    private UUID           id;
    private UUID           alcateiaId;
    private String         nome;
    private String         cor;
    private String         iconeUrl;
    private String         status;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public UUID getId()                            { return id; }
    public void setId(UUID id)                     { this.id = id; }
    public UUID getAlcateiaId()                    { return alcateiaId; }
    public void setAlcateiaId(UUID a)              { this.alcateiaId = a; }
    public String getNome()                        { return nome; }
    public void setNome(String n)                  { this.nome = n; }
    public String getCor()                         { return cor; }
    public void setCor(String c)                   { this.cor = c; }
    public String getIconeUrl()                    { return iconeUrl; }
    public void setIconeUrl(String i)              { this.iconeUrl = i; }
    public String getStatus()                      { return status; }
    public void setStatus(String s)                { this.status = s; }
    public OffsetDateTime getCriadoEm()            { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)      { this.criadoEm = t; }
    public OffsetDateTime getAtualizadoEm()        { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)  { this.atualizadoEm = t; }
}
