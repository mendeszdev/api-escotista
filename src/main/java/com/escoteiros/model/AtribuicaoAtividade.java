package com.escoteiros.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AtribuicaoAtividade {
    private UUID           id;
    private UUID           atividadeId;
    private UUID           associadoId;
    private String         status;           // enum atividade_status
    private OffsetDateTime registradoEm;
    private String         descricaoRegistro;
    private String[]       midiaUrl;         // text[] do Postgres
    private UUID           validadoPor;
    private OffsetDateTime validadoEm;
    private String         feedback;
    private LocalDate      dataRealizacao;   // quando o lobinho realizou (distinto de registradoEm)

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
    public LocalDate getDataRealizacao()             { return dataRealizacao; }
    public void setDataRealizacao(LocalDate d)       { this.dataRealizacao = d; }
}
