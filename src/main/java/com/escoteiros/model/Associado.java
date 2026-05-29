package com.escoteiros.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Associado {
    private UUID           id;
    private UUID           grupoEscoteiroId;
    private String         matricula;
    private String         senhaHash;        // NUNCA retornar em GET
    private String         perfil;           // enum perfil_tipo
    private String         nomeCompleto;
    private String         nomeEscoteiro;
    private LocalDate      dataNascimento;
    private String         genero;
    private String         estadoCivil;
    private String         cpf;
    private String         rg;
    private String         passaporte;
    private String         email;
    private String         telefone;
    private String         cep;
    private String         logradouro;
    private String         numeroEnd;
    private String         bairro;
    private String         cidade;
    private String         estado;
    private String         fotoUrl;
    private String         status;           // enum status_geral
    private boolean        documentosValidados;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    // ── Getters / Setters ───────────────────────────────────────────────
    public UUID getId()                            { return id; }
    public void setId(UUID id)                     { this.id = id; }

    public UUID getGrupoEscoteiroId()              { return grupoEscoteiroId; }
    public void setGrupoEscoteiroId(UUID g)        { this.grupoEscoteiroId = g; }

    public String getMatricula()                   { return matricula; }
    public void setMatricula(String m)             { this.matricula = m; }

    public String getSenhaHash()                   { return senhaHash; }
    public void setSenhaHash(String s)             { this.senhaHash = s; }

    public String getPerfil()                      { return perfil; }
    public void setPerfil(String p)                { this.perfil = p; }

    public String getNomeCompleto()                { return nomeCompleto; }
    public void setNomeCompleto(String n)          { this.nomeCompleto = n; }

    public String getNomeEscoteiro()               { return nomeEscoteiro; }
    public void setNomeEscoteiro(String n)         { this.nomeEscoteiro = n; }

    public LocalDate getDataNascimento()           { return dataNascimento; }
    public void setDataNascimento(LocalDate d)     { this.dataNascimento = d; }

    public String getGenero()                      { return genero; }
    public void setGenero(String g)                { this.genero = g; }

    public String getEstadoCivil()                 { return estadoCivil; }
    public void setEstadoCivil(String e)           { this.estadoCivil = e; }

    public String getCpf()                         { return cpf; }
    public void setCpf(String cpf)                 { this.cpf = cpf; }

    public String getRg()                          { return rg; }
    public void setRg(String rg)                   { this.rg = rg; }

    public String getPassaporte()                  { return passaporte; }
    public void setPassaporte(String p)            { this.passaporte = p; }

    public String getEmail()                       { return email; }
    public void setEmail(String e)                 { this.email = e; }

    public String getTelefone()                    { return telefone; }
    public void setTelefone(String t)              { this.telefone = t; }

    public String getCep()                         { return cep; }
    public void setCep(String cep)                 { this.cep = cep; }

    public String getLogradouro()                  { return logradouro; }
    public void setLogradouro(String l)            { this.logradouro = l; }

    public String getNumeroEnd()                   { return numeroEnd; }
    public void setNumeroEnd(String n)             { this.numeroEnd = n; }

    public String getBairro()                      { return bairro; }
    public void setBairro(String b)                { this.bairro = b; }

    public String getCidade()                      { return cidade; }
    public void setCidade(String c)                { this.cidade = c; }

    public String getEstado()                      { return estado; }
    public void setEstado(String e)                { this.estado = e; }

    public String getFotoUrl()                     { return fotoUrl; }
    public void setFotoUrl(String f)               { this.fotoUrl = f; }

    public String getStatus()                      { return status; }
    public void setStatus(String s)                { this.status = s; }

    public boolean isDocumentosValidados()         { return documentosValidados; }
    public void setDocumentosValidados(boolean d)  { this.documentosValidados = d; }

    public OffsetDateTime getCriadoEm()            { return criadoEm; }
    public void setCriadoEm(OffsetDateTime t)      { this.criadoEm = t; }

    public OffsetDateTime getAtualizadoEm()           { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime t)     { this.atualizadoEm = t; }
}
