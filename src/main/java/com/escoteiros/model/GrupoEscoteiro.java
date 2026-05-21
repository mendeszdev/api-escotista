package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GrupoEscoteiro {
    private UUID            id;
    private String          nome;
    private String          numero;
    private String          distrito;
    private String          regiao;
    private String          paletaCores;   // JSONB → String (pode desserializar depois)
    private String          logoUrl;
    private String          status;        // ativo | inativo | suspenso
    private OffsetDateTime  criadoEm;
    private OffsetDateTime  atualizadoEm;

    // ── Getters / Setters ───────────────────────────────────────────────
    public UUID getId()                        { return id; }
    public void setId(UUID id)                 { this.id = id; }

    public String getNome()                    { return nome; }
    public void setNome(String nome)           { this.nome = nome; }

    public String getNumero()                  { return numero; }
    public void setNumero(String numero)       { this.numero = numero; }

    public String getDistrito()                { return distrito; }
    public void setDistrito(String d)          { this.distrito = d; }

    public String getRegiao()                  { return regiao; }
    public void setRegiao(String r)            { this.regiao = r; }

    public String getPaletaCores()             { return paletaCores; }
    public void setPaletaCores(String p)       { this.paletaCores = p; }

    public String getLogoUrl()                 { return logoUrl; }
    public void setLogoUrl(String l)           { this.logoUrl = l; }

    public String getStatus()                  { return status; }
    public void setStatus(String status)       { this.status = status; }

    public OffsetDateTime getCriadoEm()        { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)  { this.criadoEm = t; }

    public OffsetDateTime getAtualizadoEm()       { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t) { this.atualizadoEm = t; }
}
