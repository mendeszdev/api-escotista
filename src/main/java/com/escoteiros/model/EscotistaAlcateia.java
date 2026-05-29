package com.escoteiros.model;

import java.time.LocalDate;
import java.util.UUID;

public class EscotistaAlcateia {
    private UUID      id;
    private UUID      associadoId;
    private UUID      alcateiaId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean   ativo;

    public UUID getId()                       { return id; }
    public void setId(UUID id)                { this.id = id; }
    public UUID getAssociadoId()              { return associadoId; }
    public void setAssociadoId(UUID a)        { this.associadoId = a; }
    public UUID getAlcateiaId()               { return alcateiaId; }
    public void setAlcateiaId(UUID a)         { this.alcateiaId = a; }
    public LocalDate getDataInicio()          { return dataInicio; }
    public void setDataInicio(LocalDate d)    { this.dataInicio = d; }
    public LocalDate getDataFim()             { return dataFim; }
    public void setDataFim(LocalDate d)       { this.dataFim = d; }
    public boolean isAtivo()                  { return ativo; }
    public void setAtivo(boolean a)           { this.ativo = a; }
}
