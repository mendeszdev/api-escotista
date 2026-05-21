package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ResponsavelLegal {
    private UUID           id;
    private UUID           associadoId;
    private String         nomeCompleto;
    private String         parentesco;
    private String         cpf;
    private String         telefone;
    private String         email;
    private boolean        responsavelFinanceiro;
    private OffsetDateTime criadoEm;

    public UUID getId()                               { return id; }
    public void setId(UUID id)                        { this.id = id; }
    public UUID getAssociadoId()                      { return associadoId; }
    public void setAssociadoId(UUID a)                { this.associadoId = a; }
    public String getNomeCompleto()                   { return nomeCompleto; }
    public void setNomeCompleto(String n)             { this.nomeCompleto = n; }
    public String getParentesco()                     { return parentesco; }
    public void setParentesco(String p)               { this.parentesco = p; }
    public String getCpf()                            { return cpf; }
    public void setCpf(String c)                      { this.cpf = c; }
    public String getTelefone()                       { return telefone; }
    public void setTelefone(String t)                 { this.telefone = t; }
    public String getEmail()                          { return email; }
    public void setEmail(String e)                    { this.email = e; }
    public boolean isResponsavelFinanceiro()          { return responsavelFinanceiro; }
    public void setResponsavelFinanceiro(boolean r)   { this.responsavelFinanceiro = r; }
    public OffsetDateTime getCriadoEm()               { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)         { this.criadoEm = t; }
}
