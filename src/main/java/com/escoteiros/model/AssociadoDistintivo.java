package com.escoteiros.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AssociadoDistintivo {
    private UUID           id;
    private UUID           associadoId;
    private UUID           distintivoId;
    private String         observacao;
    private OffsetDateTime atribuidoEm;
    private UUID           atribuidoPor;

    // Campos joinados com a tabela distintivos
    private String         distintivoNome;
    private String         distintivoDescricao;
    private String         distintivoIcone;
    private String         distintivoCorHex;
    private boolean        distintivoEEspecialidade;

    public UUID getId()                                  { return id; }
    public void setId(UUID id)                           { this.id = id; }
    public UUID getAssociadoId()                         { return associadoId; }
    public void setAssociadoId(UUID a)                   { this.associadoId = a; }
    public UUID getDistintivoId()                        { return distintivoId; }
    public void setDistintivoId(UUID d)                  { this.distintivoId = d; }
    public String getObservacao()                        { return observacao; }
    public void setObservacao(String o)                  { this.observacao = o; }
    public OffsetDateTime getAtribuidoEm()               { return atribuidoEm; }
    public void setAtribuidoEm(OffsetDateTime t)         { this.atribuidoEm = t; }
    public UUID getAtribuidoPor()                        { return atribuidoPor; }
    public void setAtribuidoPor(UUID a)                  { this.atribuidoPor = a; }

    public String getDistintivoNome()                    { return distintivoNome; }
    public void setDistintivoNome(String n)              { this.distintivoNome = n; }
    public String getDistintivoDescricao()               { return distintivoDescricao; }
    public void setDistintivoDescricao(String d)         { this.distintivoDescricao = d; }
    public String getDistintivoIcone()                   { return distintivoIcone; }
    public void setDistintivoIcone(String i)             { this.distintivoIcone = i; }
    public String getDistintivoCorHex()                  { return distintivoCorHex; }
    public void setDistintivoCorHex(String c)            { this.distintivoCorHex = c; }
    public boolean isDistintivoEEspecialidade()          { return distintivoEEspecialidade; }
    public void setDistintivoEEspecialidade(boolean e)   { this.distintivoEEspecialidade = e; }
}
