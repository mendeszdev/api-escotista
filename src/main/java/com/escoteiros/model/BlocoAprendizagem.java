package com.escoteiros.model;

import java.util.UUID;

public class BlocoAprendizagem {
    private UUID    id;
    private UUID    eixoId;
    private String  nome;
    private String  descricao;
    private String  intencionalidade;
    private int     minAcoesVariaveis;
    private int     ordem;
    private boolean ativo;

    public UUID getId()                           { return id; }
    public void setId(UUID id)                    { this.id = id; }
    public UUID getEixoId()                       { return eixoId; }
    public void setEixoId(UUID e)                 { this.eixoId = e; }
    public String getNome()                       { return nome; }
    public void setNome(String n)                 { this.nome = n; }
    public String getDescricao()                  { return descricao; }
    public void setDescricao(String d)            { this.descricao = d; }
    public String getIntencionalidade()           { return intencionalidade; }
    public void setIntencionalidade(String i)     { this.intencionalidade = i; }
    public int getMinAcoesVariaveis()             { return minAcoesVariaveis; }
    public void setMinAcoesVariaveis(int m)       { this.minAcoesVariaveis = m; }
    public int getOrdem()                         { return ordem; }
    public void setOrdem(int o)                   { this.ordem = o; }
    public boolean isAtivo()                      { return ativo; }
    public void setAtivo(boolean a)               { this.ativo = a; }
}
