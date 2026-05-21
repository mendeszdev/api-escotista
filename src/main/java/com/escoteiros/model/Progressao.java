package com.escoteiros.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Progressao {
    private UUID           id;
    private UUID           associadoId;
    private UUID           eixoId;
    private BigDecimal     percentualConcluido;
    private OffsetDateTime atualizadoEm;

    public UUID getId()                               { return id; }
    public void setId(UUID id)                        { this.id = id; }
    public UUID getAssociadoId()                      { return associadoId; }
    public void setAssociadoId(UUID a)                { this.associadoId = a; }
    public UUID getEixoId()                           { return eixoId; }
    public void setEixoId(UUID e)                     { this.eixoId = e; }
    public BigDecimal getPercentualConcluido()         { return percentualConcluido; }
    public void setPercentualConcluido(BigDecimal p)   { this.percentualConcluido = p; }
    public OffsetDateTime getAtualizadoEm()           { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)     { this.atualizadoEm = t; }
}
