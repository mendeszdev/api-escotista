package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Distintivo {
    private UUID           id;
    private String         nome;
    private String         descricao;
    private String         icone;
    private String         corHex;
    private boolean        eEspecialidade;
    private boolean        ativo;
    private OffsetDateTime criadoEm;

    public UUID getId()                      { return id; }
    public void setId(UUID id)               { this.id = id; }
    public String getNome()                  { return nome; }
    public void setNome(String n)            { this.nome = n; }
    public String getDescricao()             { return descricao; }
    public void setDescricao(String d)       { this.descricao = d; }
    public String getIcone()                 { return icone; }
    public void setIcone(String i)           { this.icone = i; }
    public String getCorHex()               { return corHex; }
    public void setCorHex(String c)          { this.corHex = c; }
    public boolean isEEspecialidade()        { return eEspecialidade; }
    public void setEEspecialidade(boolean e) { this.eEspecialidade = e; }
    public boolean isAtivo()                 { return ativo; }
    public void setAtivo(boolean a)          { this.ativo = a; }
    public OffsetDateTime getCriadoEm()      { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t){ this.criadoEm = t; }
}
