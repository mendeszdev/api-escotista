package com.escoteiros.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AtribuicaoDetalhada {
    private UUID           id;
    private UUID           atividadeId;
    private UUID           associadoId;
    private String         status;
    private OffsetDateTime registradoEm;
    private String         descricaoRegistro;
    private String[]       midiaUrl;
    private UUID           validadoPor;
    private OffsetDateTime validadoEm;
    private String         feedback;

    private LocalDate      dataRealizacao;
    private UUID           acaoEducativaId;
    private String         acaoTipo;

    // Campos joinados
    private String         associadoNome;
    private String         atividadeNome;
    private String         blocoNome;
    private UUID           blocoId;
    private String         eixoNome;
    private UUID           eixoId;
    private LocalDate      dataLimite;

    public UUID getId()                              { return id; }
    public void setId(UUID id)                       { this.id = id; }
    public UUID getAtividadeId()                     { return atividadeId; }
    public void setAtividadeId(UUID a)               { this.atividadeId = a; }
    public UUID getAssociadoId()                     { return associadoId; }
    public void setAssociadoId(UUID a)               { this.associadoId = a; }
    public String getStatus()                        { return status; }
    public void setStatus(String s)                  { this.status = s; }
    public OffsetDateTime getRegistradoEm()          { return registradoEm; }
    public void setRegistradoEm(OffsetDateTime t)    { this.registradoEm = t; }
    public String getDescricaoRegistro()             { return descricaoRegistro; }
    public void setDescricaoRegistro(String d)       { this.descricaoRegistro = d; }
    public String[] getMidiaUrl()                    { return midiaUrl; }
    public void setMidiaUrl(String[] m)              { this.midiaUrl = m; }
    public UUID getValidadoPor()                     { return validadoPor; }
    public void setValidadoPor(UUID v)               { this.validadoPor = v; }
    public OffsetDateTime getValidadoEm()            { return validadoEm; }
    public void setValidadoEm(OffsetDateTime t)      { this.validadoEm = t; }
    public String getFeedback()                      { return feedback; }
    public void setFeedback(String f)                { this.feedback = f; }

    public String getAssociadoNome()                 { return associadoNome; }
    public void setAssociadoNome(String n)           { this.associadoNome = n; }
    public String getAtividadeNome()                 { return atividadeNome; }
    public void setAtividadeNome(String n)           { this.atividadeNome = n; }
    public String getBlocoNome()                     { return blocoNome; }
    public void setBlocoNome(String n)               { this.blocoNome = n; }
    public UUID getBlocoId()                         { return blocoId; }
    public void setBlocoId(UUID id)                  { this.blocoId = id; }
    public String getEixoNome()                      { return eixoNome; }
    public void setEixoNome(String n)                { this.eixoNome = n; }
    public UUID getEixoId()                          { return eixoId; }
    public void setEixoId(UUID id)                   { this.eixoId = id; }
    public LocalDate getDataRealizacao()             { return dataRealizacao; }
    public void setDataRealizacao(LocalDate d)       { this.dataRealizacao = d; }
    public LocalDate getDataLimite()                 { return dataLimite; }
    public void setDataLimite(LocalDate d)           { this.dataLimite = d; }
    public UUID getAcaoEducativaId()                 { return acaoEducativaId; }
    public void setAcaoEducativaId(UUID a)           { this.acaoEducativaId = a; }
    public String getAcaoTipo()                      { return acaoTipo; }
    public void setAcaoTipo(String t)                { this.acaoTipo = t; }
}
