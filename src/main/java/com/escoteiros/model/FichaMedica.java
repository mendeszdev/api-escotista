package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class FichaMedica {
    private UUID           id;
    private UUID           associadoId;
    private String         tipoSanguineo;
    private String[]       alergias;
    private String[]       condicoes;
    private String[]       medicamentos;
    private String         observacoes;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public UUID getId()                            { return id; }
    public void setId(UUID id)                     { this.id = id; }
    public UUID getAssociadoId()                   { return associadoId; }
    public void setAssociadoId(UUID a)             { this.associadoId = a; }
    public String getTipoSanguineo()               { return tipoSanguineo; }
    public void setTipoSanguineo(String t)         { this.tipoSanguineo = t; }
    public String[] getAlergias()                  { return alergias; }
    public void setAlergias(String[] a)            { this.alergias = a; }
    public String[] getCondicoes()                 { return condicoes; }
    public void setCondicoes(String[] c)           { this.condicoes = c; }
    public String[] getMedicamentos()              { return medicamentos; }
    public void setMedicamentos(String[] m)        { this.medicamentos = m; }
    public String getObservacoes()                 { return observacoes; }
    public void setObservacoes(String o)           { this.observacoes = o; }
    public OffsetDateTime getCriadoEm()            { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)      { this.criadoEm = t; }
    public OffsetDateTime getAtualizadoEm()        { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)  { this.atualizadoEm = t; }
}
