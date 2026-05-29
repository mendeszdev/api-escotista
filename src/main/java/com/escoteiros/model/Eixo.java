package com.escoteiros.model;

import java.util.UUID;

public class Eixo {
    private UUID   id;
    private String tipo;
    private String nome;
    private String descricao;
    private String icone;
    private String corHex;
    private int    ordem;

    public UUID getId()                   { return id; }
    public void setId(UUID id)            { this.id = id; }
    public String getTipo()               { return tipo; }
    public void setTipo(String t)         { this.tipo = t; }
    public String getNome()               { return nome; }
    public void setNome(String n)         { this.nome = n; }
    public String getDescricao()          { return descricao; }
    public void setDescricao(String d)    { this.descricao = d; }
    public String getIcone()              { return icone; }
    public void setIcone(String i)        { this.icone = i; }
    public String getCorHex()             { return corHex; }
    public void setCorHex(String c)       { this.corHex = c; }
    public int getOrdem()                 { return ordem; }
    public void setOrdem(int o)           { this.ordem = o; }
}
