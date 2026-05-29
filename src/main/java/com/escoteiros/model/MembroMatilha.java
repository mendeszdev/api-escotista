package com.escoteiros.model;

import java.time.LocalDate;
import java.util.UUID;

public class MembroMatilha {
    private UUID      id;
    private UUID      matilhaId;
    private UUID      associadoId;
    private String    cargo;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private boolean   ativo;

    public UUID getId()                       { return id; }
    public void setId(UUID id)                { this.id = id; }
    public UUID getMatilhaId()                { return matilhaId; }
    public void setMatilhaId(UUID m)          { this.matilhaId = m; }
    public UUID getAssociadoId()              { return associadoId; }
    public void setAssociadoId(UUID a)        { this.associadoId = a; }
    public String getCargo()                  { return cargo; }
    public void setCargo(String c)            { this.cargo = c; }
    public LocalDate getDataEntrada()         { return dataEntrada; }
    public void setDataEntrada(LocalDate d)   { this.dataEntrada = d; }
    public LocalDate getDataSaida()           { return dataSaida; }
    public void setDataSaida(LocalDate d)     { this.dataSaida = d; }
    public boolean isAtivo()                  { return ativo; }
    public void setAtivo(boolean a)           { this.ativo = a; }
}
