# BACKEND_ANALYSIS.md
> Gerado em: 2026-05-29 | Versão analisada: branch `main`

---

## 1. VISÃO GERAL DA ARQUITETURA

| Item | Detalhe |
|---|---|
| **Framework** | Jakarta EE 10 — Servlets puros (sem Spring) |
| **Java** | 17 |
| **Build** | Maven — empacotamento WAR |
| **Servidor** | Apache Tomcat 10+ |
| **Banco de dados** | PostgreSQL (via Supabase) — JDBC direto, sem ORM |
| **Autenticação** | JWT (JJWT 0.12.6) + BCrypt (jbcrypt 0.4) |
| **Serialização JSON** | Gson 2.10.1 com adaptadores customizados |
| **Config** | Arquivo `.env` lido por dotenv-java |

### Camadas

```
HTTP Request
    │
    ▼
[CorsFilter]  →  adiciona headers CORS
    │
    ▼
[AuthFilter]  →  valida Bearer token em /api/* (exceto login/refresh)
    │
    ▼
[Servlet]     →  recebe, parseia e responde (sem Service layer)
    │
    ▼
[DAO]         →  executa SQL via JDBC
    │
    ▼
[PostgreSQL]  →  banco com ENUMs e trigger de progressão
```

**Nota:** Não há camada de Service. A lógica de negócio está diretamente nos Servlets e DAOs.

---

## 2. ENTIDADES E CAMPOS

### Convenções
- Todos os IDs são **UUID v4**
- `criado_em` / `atualizado_em` são gerados automaticamente pelo banco
- Campos com `*` são **obrigatórios** no POST

---

### 2.1 GrupoEscoteiro
Tabela: `grupos_escoteiros`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK, gerado pelo banco |
| `nome` * | `nome` | String | |
| `numero` * | `numero` | String | Número oficial do grupo |
| `distrito` | `distrito` | String | |
| `regiao` | `regiao` | String | |
| `paletaCores` | `paleta_cores` | String (JSONB) | JSON de cores do grupo |
| `logoUrl` | `logo_url` | String | URL da logo |
| `status` | `status` | Enum `status_geral` | `ativo`\|`inativo`\|`suspenso` |
| `criadoEm` | `criado_em` | OffsetDateTime | Gerado pelo banco |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | Gerado pelo banco |

---

### 2.2 Associado
Tabela: `associados`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK, gerado pelo banco |
| `grupoEscoteiroId` * | `grupo_escoteiro_id` | UUID | FK → grupos_escoteiros |
| `matricula` * | `matricula` | String | Único por grupo |
| `senha` * (POST) | `senha_hash` | String | Nunca retornado em GET; hash BCrypt |
| `perfil` * | `perfil` | Enum `perfil_tipo` | Ver valores abaixo |
| `nomeCompleto` * | `nome_completo` | String | |
| `nomeEscoteiro` | `nome_escoteiro` | String | Apelido escoteiro |
| `dataNascimento` | `data_nascimento` | LocalDate | Formato: `YYYY-MM-DD` |
| `genero` | `genero` | String | |
| `estadoCivil` | `estado_civil` | String | |
| `cpf` | `cpf` | String | |
| `rg` | `rg` | String | |
| `passaporte` | `passaporte` | String | |
| `email` | `email` | String | |
| `telefone` | `telefone` | String | |
| `cep` | `cep` | String | |
| `logradouro` | `logradouro` | String | |
| `numeroEnd` | `numero_end` | String | |
| `bairro` | `bairro` | String | |
| `cidade` | `cidade` | String | |
| `estado` | `estado` | String | Sigla UF |
| `fotoUrl` | `foto_url` | String | URL da foto |
| `status` | `status` | Enum `status_geral` | Padrão: `ativo` |
| `documentosValidados` | `documentos_validados` | boolean | Padrão: `false` |
| `criadoEm` | `criado_em` | OffsetDateTime | Gerado pelo banco |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | Gerado pelo banco |

**Enum `perfil_tipo`:** `lobinho` | `escoteiro` | `senior` | `pioneiro` | `escotista` | `dirigente`

**Campos atualizáveis via PUT** (não inclui matricula, perfil, cpf, rg, passaporte):
`nomeCompleto`, `nomeEscoteiro`, `genero`, `estadoCivil`, `email`, `telefone`, `cep`, `logradouro`, `numeroEnd`, `bairro`, `cidade`, `estado`, `fotoUrl`, `status`, `documentosValidados`

---

### 2.3 Alcateia
Tabela: `alcateias`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `grupoEscoteiroId` * | `grupo_escoteiro_id` | UUID | FK → grupos_escoteiros |
| `nome` * | `nome` | String | |
| `descricao` | `descricao` | String | |
| `status` | `status` | Enum `status_geral` | |
| `criadoEm` | `criado_em` | OffsetDateTime | |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | |

---

### 2.4 Matilha
Tabela: `matilhas`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `alcateiaId` * | `alcateia_id` | UUID | FK → alcateias |
| `nome` * | `nome` | String | |
| `cor` | `cor` | String | Cor da matilha |
| `iconeUrl` | `icone_url` | String | URL do ícone |
| `status` | `status` | Enum `status_geral` | |
| `criadoEm` | `criado_em` | OffsetDateTime | |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | |

---

### 2.5 MembroMatilha
Tabela: `membros_matilha`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `matilhaId` * | `matilha_id` | UUID | FK → matilhas |
| `associadoId` * | `associado_id` | UUID | FK → associados |
| `cargo` | `cargo` | Enum `cargo_matilha` | Padrão: `membro` |
| `dataEntrada` | `data_entrada` | LocalDate | Gerado pelo banco |
| `dataSaida` | `data_saida` | LocalDate | Preenchido ao encerrar |
| `ativo` | `ativo` | boolean | Padrão: `true` |

**Enum `cargo_matilha`:** `membro` | `lider` | `sublider`

---

### 2.6 EscotistaAlcateia
Tabela: `escotistas_alcateias`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `associadoId` * | `associado_id` | UUID | FK → associados (deve ter perfil escotista/dirigente) |
| `alcateiaId` * | `alcateia_id` | UUID | FK → alcateias |
| `dataInicio` | `data_inicio` | LocalDate | Gerado pelo banco |
| `dataFim` | `data_fim` | LocalDate | Preenchido ao encerrar |
| `ativo` | `ativo` | boolean | Padrão: `true` |

---

### 2.7 Eixo
Tabela: `eixos`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `tipo` * | `tipo` | String | Tipo/categoria do eixo |
| `nome` * | `nome` | String | |
| `descricao` | `descricao` | String | |
| `icone` | `icone` | String | Identificador de ícone |
| `corHex` | `cor_hex` | String | Cor em hex `#RRGGBB` |
| `ordem` | `ordem` | int | Ordem de exibição |

---

### 2.8 BlocoAprendizagem
Tabela: `blocos_aprendizagem`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `eixoId` * | `eixo_id` | UUID | FK → eixos |
| `nome` * | `nome` | String | |
| `descricao` | `descricao` | String | |
| `minAcoesVariaveis` | `min_acoes_variaveis` | int | Mínimo de ações livres exigidas |
| `ordem` | `ordem` | int | Ordem dentro do eixo |
| `ativo` | `ativo` | boolean | |

---

### 2.9 AcaoEducativa
Tabela: `acoes_educativas`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `blocoId` * | `bloco_id` | UUID | FK → blocos_aprendizagem |
| `nome` * | `nome` | String | |
| `descricao` | `descricao` | String | |
| `tipo` | `tipo` | Enum `acao_tipo` | Tipo da ação educativa |
| `eEspecialidade` | `e_especialidade` | boolean | |
| `criadoPor` | `criado_por` | UUID | FK → associados |
| `ativo` | `ativo` | boolean | Padrão: `true` |
| `criadoEm` | `criado_em` | OffsetDateTime | |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | |

---

### 2.10 Atividade
Tabela: `atividades`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `acaoEducativaId` * | `acao_educativa_id` | UUID | FK → acoes_educativas |
| `criadoPor` * | `criado_por` | UUID | FK → associados (quem criou) |
| `alcateiaId` | `alcateia_id` | UUID | FK → alcateias (se personalizada) |
| `ePersonalizada` | `e_personalizada` | boolean | Padrão: `false` |
| `nomePersonalizado` | `nome_personalizado` | String | Só se `ePersonalizada = true` |
| `descricaoPersonalizada` | `descricao_personalizada` | String | Só se `ePersonalizada = true` |
| `aprovadaPor` | `aprovada_por` | UUID | FK → associados (quem aprovou) |
| `dataLimite` | `data_limite` | LocalDate | Formato: `YYYY-MM-DD` |
| `criadoEm` | `criado_em` | OffsetDateTime | |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | |

---

### 2.11 AtribuicaoAtividade
Tabela: `atribuicoes_atividade`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `atividadeId` * | `atividade_id` | UUID | FK → atividades |
| `associadoId` * | `associado_id` | UUID | FK → associados |
| `status` | `status` | Enum `atividade_status` | Padrão: `pendente` |
| `registradoEm` | `registrado_em` | OffsetDateTime | Gerado pelo banco |
| `descricaoRegistro` | `descricao_registro` | String | Relato de conclusão |
| `midiaUrl` | `midia_url` | String[] | Array de URLs de evidência |
| `validadoPor` | `validado_por` | UUID | FK → associados (validador) |
| `validadoEm` | `validado_em` | OffsetDateTime | Data da validação |
| `feedback` | `feedback` | String | Feedback do validador |

**Enum `atividade_status`:** `pendente` | `em_andamento` | `concluida` | `cancelada`

---

### 2.12 Distintivo
Tabela: `distintivos`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `nome` * | `nome` | String | |
| `descricao` | `descricao` | String | |
| `icone` | `icone` | String | Identificador de ícone |
| `corHex` | `cor_hex` | String | Cor em hex |
| `eEspecialidade` | `e_especialidade` | boolean | |
| `ativo` | `ativo` | boolean | |
| `criadoEm` | `criado_em` | OffsetDateTime | |

---

### 2.13 Documento
Tabela: `documentos`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `associadoId` * | `associado_id` | UUID | FK → associados |
| `tipo` * | `tipo` | String | Ex: `CPF`, `RG`, `atestado_medico` |
| `nomeArquivo` * | `nome_arquivo` | String | Nome do arquivo |
| `url` * | `url` | String | URL de acesso ao arquivo |
| `tamanhoBytes` | `tamanho_bytes` | Long | Tamanho em bytes |
| `validade` | `validade` | LocalDate | Data de validade do documento |
| `obrigatorio` | `obrigatorio` | boolean | |
| `status` | `status` | String | Estado do documento |
| `criadoEm` | `criado_em` | OffsetDateTime | |

---

### 2.14 FichaMedica
Tabela: `fichas_medicas`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `associadoId` * | `associado_id` | UUID | FK → associados (UNIQUE) |
| `tipoSanguineo` | `tipo_sanguineo` | Enum `tipo_sanguineo` | Ver valores abaixo |
| `alergias` | `alergias` | String[] | Array de texto |
| `condicoes` | `condicoes` | String[] | Array de condições médicas |
| `medicamentos` | `medicamentos` | String[] | Array de medicamentos |
| `observacoes` | `observacoes` | String | |
| `criadoEm` | `criado_em` | OffsetDateTime | |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | |

**Enum `tipo_sanguineo`:** `A_POSITIVO` | `A_NEGATIVO` | `B_POSITIVO` | `B_NEGATIVO` | `AB_POSITIVO` | `AB_NEGATIVO` | `O_POSITIVO` | `O_NEGATIVO`

---

### 2.15 Progressao
Tabela: `progressoes`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `associadoId` | `associado_id` | UUID | FK → associados |
| `eixoId` | `eixo_id` | UUID | FK → eixos |
| `percentualConcluido` | `percentual_concluido` | BigDecimal | 0.00 a 100.00 |
| `atualizadoEm` | `atualizado_em` | OffsetDateTime | |

**Leitura apenas.** Atualizada automaticamente pelo trigger `fn_atualizar_progressao` no banco.

---

### 2.16 ResponsavelLegal
Tabela: `responsaveis_legais`

| Campo Java | Coluna DB | Tipo | Notas |
|---|---|---|---|
| `id` | `id` | UUID | PK |
| `associadoId` * | `associado_id` | UUID | FK → associados |
| `nomeCompleto` * | `nome_completo` | String | |
| `parentesco` * | `parentesco` | String | Ex: `pai`, `mãe`, `tutor` |
| `cpf` | `cpf` | String | |
| `telefone` * | `telefone` | String | |
| `email` | `email` | String | |
| `responsavelFinanceiro` | `responsavel_financeiro` | boolean | |
| `criadoEm` | `criado_em` | OffsetDateTime | |

---

## 3. MAPA DE RELACIONAMENTOS

```
GrupoEscoteiro
    │
    ├─1:N─► Associado (grupo_escoteiro_id)
    │           │
    │           ├─1:N─► MembroMatilha (associado_id)
    │           ├─1:N─► EscotistaAlcateia (associado_id)
    │           ├─1:N─► AtribuicaoAtividade (associado_id)
    │           ├─1:N─► Documento (associado_id)
    │           ├─1:1─► FichaMedica (associado_id UNIQUE)
    │           ├─1:N─► Progressao (associado_id)
    │           └─1:N─► ResponsavelLegal (associado_id)
    │
    └─1:N─► Alcateia (grupo_escoteiro_id)
                │
                ├─1:N─► Matilha (alcateia_id)
                │           └─1:N─► MembroMatilha (matilha_id)
                │
                ├─1:N─► EscotistaAlcateia (alcateia_id)
                └─1:N─► Atividade (alcateia_id)

Eixo
  └─1:N─► BlocoAprendizagem (eixo_id)
               └─1:N─► AcaoEducativa (bloco_id)
                            └─1:N─► Atividade (acao_educativa_id)
                                         └─1:N─► AtribuicaoAtividade (atividade_id)

Progressao: associado_id + eixo_id (calculado por trigger)
```

---

## 4. ENDPOINTS — REFERÊNCIA COMPLETA

### Cabeçalho obrigatório em todas as rotas protegidas
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

### Rotas públicas (sem token)
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `OPTIONS *` (preflight CORS)

---

### 4.1 Autenticação

#### POST /api/auth/login
**Corpo da requisição:**
```json
{
  "matricula": "string (obrigatório)",
  "senha":     "string (obrigatório)"
}
```
**Resposta 200:**
```json
{
  "success":      true,
  "accessToken":  "jwt — expira em 30 min",
  "refreshToken": "jwt — expira em 7 dias",
  "expiresIn":    1800,
  "user": {
    "id":           "uuid",
    "name":         "string",
    "nomeEscoteiro":"string",
    "matricula":    "string",
    "role":         "dirigente | escotista | lobinho",
    "email":        "string",
    "fotoUrl":      "string | null",
    "grupoId":      "uuid | null"
  }
}
```
**Erros:** 400 (campos faltando), 401 (credenciais inválidas)

---

#### POST /api/auth/refresh
**Corpo da requisição:**
```json
{
  "refreshToken": "string (obrigatório)"
}
```
**Resposta 200:**
```json
{
  "accessToken":  "string",
  "refreshToken": "string",
  "expiresIn":    1800
}
```
**Erros:** 400 (token faltando), 401 (token inválido/expirado, usuário inativo)

---

#### POST /api/auth/logout
**Header:** `Authorization: Bearer <accessToken>`  
**Resposta 200:** `{ "mensagem": "Logout realizado com sucesso" }`  
> **⚠ Bug:** NullPointerException se o header `Authorization` estiver ausente.

---

### 4.2 Grupos Escoteiros — `/api/grupos-escoteiros`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/grupos-escoteiros` | Lista todos |
| GET | `/api/grupos-escoteiros/{id}` | Busca por ID |
| POST | `/api/grupos-escoteiros` | Cria novo |
| PUT | `/api/grupos-escoteiros/{id}` | Atualiza |
| DELETE | `/api/grupos-escoteiros/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "nome":        "string (obrigatório)",
  "numero":      "string (obrigatório)",
  "distrito":    "string (opcional)",
  "regiao":      "string (opcional)",
  "paletaCores": "string JSON (opcional)",
  "logoUrl":     "string (opcional)",
  "status":      "ativo | inativo | suspenso (opcional, padrão: ativo)"
}
```

---

### 4.3 Associados — `/api/associados`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/associados` | Lista todos |
| GET | `/api/associados?grupo={uuid}` | Filtra por grupo |
| GET | `/api/associados?perfil={perfil_tipo}` | Filtra por perfil |
| GET | `/api/associados/{id}` | Busca por ID |
| POST | `/api/associados` | Cria novo |
| PUT | `/api/associados/{id}` | Atualiza dados editáveis |
| DELETE | `/api/associados/{id}` | Remove |

**POST — Campos obrigatórios e opcionais:**
```json
{
  "grupoEscoteiroId": "uuid (obrigatório)",
  "matricula":        "string (obrigatório)",
  "senha":            "string (obrigatório — será hasheada)",
  "perfil":           "lobinho|escoteiro|senior|pioneiro|escotista|dirigente (obrigatório)",
  "nomeCompleto":     "string (obrigatório)",
  "nomeEscoteiro":    "string (opcional)",
  "dataNascimento":   "YYYY-MM-DD (opcional)",
  "genero":           "string (opcional)",
  "estadoCivil":      "string (opcional)",
  "cpf":              "string (opcional)",
  "rg":               "string (opcional)",
  "passaporte":       "string (opcional)",
  "email":            "string (opcional)",
  "telefone":         "string (opcional)",
  "cep":              "string (opcional)",
  "logradouro":       "string (opcional)",
  "numeroEnd":        "string (opcional)",
  "bairro":           "string (opcional)",
  "cidade":           "string (opcional)",
  "estado":           "string (opcional — sigla UF)",
  "fotoUrl":          "string (opcional)",
  "status":           "ativo|inativo|suspenso (opcional, padrão: ativo)"
}
```

**PUT — Campos atualizáveis** (matricula, cpf, rg, perfil, passaporte NÃO podem ser alterados):
```json
{
  "nomeCompleto":      "string",
  "nomeEscoteiro":     "string",
  "genero":            "string",
  "estadoCivil":       "string",
  "email":             "string",
  "telefone":          "string",
  "cep":               "string",
  "logradouro":        "string",
  "numeroEnd":         "string",
  "bairro":            "string",
  "cidade":            "string",
  "estado":            "string",
  "fotoUrl":           "string",
  "status":            "ativo|inativo|suspenso",
  "documentosValidados": true
}
```

---

### 4.4 Alcateias — `/api/alcateias`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/alcateias` | Lista todas |
| GET | `/api/alcateias?grupo={uuid}` | Filtra por grupo |
| GET | `/api/alcateias/{id}` | Busca por ID |
| POST | `/api/alcateias` | Cria nova |
| PUT | `/api/alcateias/{id}` | Atualiza |
| DELETE | `/api/alcateias/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "grupoEscoteiroId": "uuid (obrigatório)",
  "nome":             "string (obrigatório)",
  "descricao":        "string (opcional)",
  "status":           "ativo|inativo|suspenso (opcional)"
}
```

---

### 4.5 Matilhas — `/api/matilhas`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/matilhas` | Lista todas |
| GET | `/api/matilhas?alcateia={uuid}` | Filtra por alcateia |
| GET | `/api/matilhas/{id}` | Busca por ID |
| POST | `/api/matilhas` | Cria nova |
| PUT | `/api/matilhas/{id}` | Atualiza |
| DELETE | `/api/matilhas/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "alcateiaId": "uuid (obrigatório)",
  "nome":       "string (obrigatório)",
  "cor":        "string (opcional)",
  "iconeUrl":   "string (opcional)",
  "status":     "ativo|inativo|suspenso (opcional)"
}
```

---

### 4.6 Membros de Matilha — `/api/membros-matilha`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/membros-matilha?matilha={uuid}` | Lista membros ativos de uma matilha |
| POST | `/api/membros-matilha` | Adiciona membro à matilha |
| PUT | `/api/membros-matilha/{id}` | Encerra participação (`ativo=false`, `dataSaida=hoje`) |
| DELETE | `/api/membros-matilha/{id}` | Remove permanentemente |

> **Atenção:** GET sem `?matilha=` retorna erro 400. Não existe GET por ID.

**POST — Campos:**
```json
{
  "matilhaId":   "uuid (obrigatório)",
  "associadoId": "uuid (obrigatório)",
  "cargo":       "membro|lider|sublider (opcional, padrão: membro)"
}
```

---

### 4.7 Escotistas de Alcateia — `/api/escotistas-alcateias`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/escotistas-alcateias?alcateia={uuid}` | Lista escotistas ativos da alcateia |
| GET | `/api/escotistas-alcateias?associado={uuid}` | Lista alcateias de um associado |
| POST | `/api/escotistas-alcateias` | Vincula escotista à alcateia |
| PUT | `/api/escotistas-alcateias/{id}` | Encerra vínculo (`ativo=false`, `dataFim=hoje`) |
| DELETE | `/api/escotistas-alcateias/{id}` | Remove permanentemente |

> **Atenção:** GET sem `?alcateia=` ou `?associado=` retorna erro 400.

**POST — Campos:**
```json
{
  "associadoId": "uuid (obrigatório)",
  "alcateiaId":  "uuid (obrigatório)"
}
```

---

### 4.8 Eixos — `/api/eixos`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/eixos` | Lista todos |
| GET | `/api/eixos/{id}` | Busca por ID |
| POST | `/api/eixos` | Cria novo |
| PUT | `/api/eixos/{id}` | Atualiza |
| DELETE | `/api/eixos/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "tipo":     "string (obrigatório)",
  "nome":     "string (obrigatório)",
  "descricao":"string (opcional)",
  "icone":    "string (opcional)",
  "corHex":   "string hex #RRGGBB (opcional)",
  "ordem":    0
}
```

---

### 4.9 Blocos de Aprendizagem — `/api/blocos-aprendizagem`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/blocos-aprendizagem` | Lista todos |
| GET | `/api/blocos-aprendizagem?eixo={uuid}` | Filtra por eixo |
| GET | `/api/blocos-aprendizagem/{id}` | Busca por ID |
| POST | `/api/blocos-aprendizagem` | Cria novo |
| PUT | `/api/blocos-aprendizagem/{id}` | Atualiza |
| DELETE | `/api/blocos-aprendizagem/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "eixoId":            "uuid (obrigatório)",
  "nome":              "string (obrigatório)",
  "descricao":         "string (opcional)",
  "minAcoesVariaveis": 0,
  "ordem":             0,
  "ativo":             true
}
```

---

### 4.10 Ações Educativas — `/api/acoes-educativas`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/acoes-educativas` | Lista todas |
| GET | `/api/acoes-educativas?bloco={uuid}` | Filtra por bloco |
| GET | `/api/acoes-educativas/{id}` | Busca por ID |
| POST | `/api/acoes-educativas` | Cria nova |
| PUT | `/api/acoes-educativas/{id}` | Atualiza |
| DELETE | `/api/acoes-educativas/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "blocoId":        "uuid (obrigatório)",
  "nome":           "string (obrigatório)",
  "descricao":      "string (opcional)",
  "tipo":           "string enum acao_tipo (opcional)",
  "eEspecialidade": false,
  "criadoPor":      "uuid (opcional — id do associado criador)",
  "ativo":          true
}
```

---

### 4.11 Atividades — `/api/atividades`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/atividades` | Lista todas |
| GET | `/api/atividades?alcateia={uuid}` | Filtra por alcateia |
| GET | `/api/atividades/{id}` | Busca por ID |
| POST | `/api/atividades` | Cria nova |
| PUT | `/api/atividades/{id}` | Atualiza |
| DELETE | `/api/atividades/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "acaoEducativaId":        "uuid (obrigatório)",
  "criadoPor":              "uuid (obrigatório — id do associado)",
  "alcateiaId":             "uuid (opcional — obrigatório se personalizada)",
  "ePersonalizada":         false,
  "nomePersonalizado":      "string (opcional — usar se personalizada)",
  "descricaoPersonalizada": "string (opcional — usar se personalizada)",
  "aprovadaPor":            "uuid (opcional)",
  "dataLimite":             "YYYY-MM-DD (opcional)"
}
```

---

### 4.12 Atribuições de Atividade — `/api/atribuicoes`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/atribuicoes` | Lista todas |
| GET | `/api/atribuicoes?associado={uuid}` | Filtra por associado |
| GET | `/api/atribuicoes?atividade={uuid}` | Filtra por atividade |
| GET | `/api/atribuicoes/{id}` | Busca por ID |
| POST | `/api/atribuicoes` | Cria nova atribuição |
| PUT | `/api/atribuicoes/{id}` | Atualiza (registrar progresso ou validar) |
| DELETE | `/api/atribuicoes/{id}` | Remove |

**POST — Criar atribuição:**
```json
{
  "atividadeId":      "uuid (obrigatório)",
  "associadoId":      "uuid (obrigatório)",
  "status":           "pendente|em_andamento|concluida|cancelada (opcional, padrão: pendente)",
  "descricaoRegistro":"string (opcional)",
  "midiaUrl":         ["url1", "url2"] 
}
```

**PUT — Registrar conclusão / validar:**
```json
{
  "status":           "pendente|em_andamento|concluida|cancelada",
  "registradoEm":     "ISO 8601 datetime (opcional)",
  "descricaoRegistro":"string (opcional)",
  "midiaUrl":         ["url1", "url2"],
  "validadoPor":      "uuid (opcional — id do validador)",
  "validadoEm":       "ISO 8601 datetime (opcional)",
  "feedback":         "string (opcional)"
}
```

---

### 4.13 Distintivos — `/api/distintivos`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/distintivos` | Lista todos |
| GET | `/api/distintivos/{id}` | Busca por ID |
| POST | `/api/distintivos` | Cria novo |
| PUT | `/api/distintivos/{id}` | Atualiza |
| DELETE | `/api/distintivos/{id}` | Remove |

**POST/PUT — Campos:**
```json
{
  "nome":           "string (obrigatório)",
  "descricao":      "string (opcional)",
  "icone":          "string (opcional)",
  "corHex":         "string hex (opcional)",
  "eEspecialidade": false,
  "ativo":          true
}
```

---

### 4.14 Documentos — `/api/documentos`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/documentos?associado={uuid}` | Lista documentos do associado |
| GET | `/api/documentos/{id}` | Busca por ID |
| POST | `/api/documentos` | Cria novo documento |
| PUT | `/api/documentos/{id}` | Atualiza documento |
| DELETE | `/api/documentos/{id}` | Remove |

> **Atenção:** GET sem `?associado=` e sem `/{id}` retorna erro 400.

**POST — Campos:**
```json
{
  "associadoId":  "uuid (obrigatório)",
  "tipo":         "string (obrigatório — ex: CPF, RG, atestado_medico)",
  "nomeArquivo":  "string (obrigatório)",
  "url":          "string (obrigatório — URL do arquivo)",
  "tamanhoBytes": 1024,
  "validade":     "YYYY-MM-DD (opcional)",
  "obrigatorio":  false,
  "status":       "string (opcional)"
}
```

---

### 4.15 Ficha Médica — `/api/fichas-medicas`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/fichas-medicas?associado={uuid}` | Busca ficha do associado |
| POST | `/api/fichas-medicas` | Cria ou atualiza (upsert) |

> Apenas uma ficha por associado. POST faz INSERT ON CONFLICT UPDATE.

**POST — Campos:**
```json
{
  "associadoId":  "uuid (obrigatório)",
  "tipoSanguineo":"A_POSITIVO|A_NEGATIVO|B_POSITIVO|B_NEGATIVO|AB_POSITIVO|AB_NEGATIVO|O_POSITIVO|O_NEGATIVO (opcional)",
  "alergias":     ["string", "string"],
  "condicoes":    ["string", "string"],
  "medicamentos": ["string", "string"],
  "observacoes":  "string (opcional)"
}
```

---

### 4.16 Progressão — `/api/progressoes`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/progressoes` | Lista todas as progressões |
| GET | `/api/progressoes?associado={uuid}` | Lista progressão por eixo de um associado |

> **Somente leitura.** Atualizada automaticamente pelo trigger `fn_atualizar_progressao` quando atribuições são concluídas.

**Resposta:**
```json
[
  {
    "id":                  "uuid",
    "associadoId":         "uuid",
    "eixoId":              "uuid",
    "percentualConcluido": 45.50,
    "atualizadoEm":        "ISO 8601"
  }
]
```

---

### 4.17 Responsáveis Legais — `/api/responsaveis`

| Método | Rota / Query | Descrição |
|---|---|---|
| GET | `/api/responsaveis?associado={uuid}` | Lista responsáveis do associado |
| GET | `/api/responsaveis/{id}` | Busca por ID |
| POST | `/api/responsaveis` | Cria novo responsável |
| PUT | `/api/responsaveis/{id}` | Atualiza |
| DELETE | `/api/responsaveis/{id}` | Remove |

> **Atenção:** GET sem `?associado=` e sem `/{id}` retorna erro 400.

**POST/PUT — Campos:**
```json
{
  "associadoId":          "uuid (obrigatório)",
  "nomeCompleto":         "string (obrigatório)",
  "parentesco":           "string (obrigatório — ex: pai, mãe, tutor)",
  "cpf":                  "string (opcional)",
  "telefone":             "string (obrigatório)",
  "email":                "string (opcional)",
  "responsavelFinanceiro": false
}
```

---

## 5. REGRAS DE NEGÓCIO IMPLEMENTADAS

### 5.1 Autenticação
- Login válido apenas para associados com `status = 'ativo'`
- Tokens JWT: `accessToken` válido por 30 min, `refreshToken` por 7 dias
- Refresh também valida que o associado ainda existe e está ativo
- Logout revoga o token no `TokenStore` (in-memory)
- Mapeamento de perfil para role no JWT: apenas `dirigente` e `escotista` têm roles distintas; todos os outros perfis (`lobinho`, `escoteiro`, `senior`, `pioneiro`) mapeiam para role `"lobinho"`

### 5.2 Senhas
- Hash BCrypt com custo 12 na criação
- Hash nunca retornado em resposta GET
- `senhaHash` limpo para `null` antes de retornar o objeto após inserção

### 5.3 Membros e Vínculos
- `MembroMatilha.PUT /{id}` faz soft-delete: seta `ativo=false` e `data_saida=CURRENT_DATE`
- `EscotistaAlcateia.PUT /{id}` faz soft-delete: seta `ativo=false` e `data_fim=CURRENT_DATE`
- GET de membros de matilha filtra apenas `ativo=true`
- GET de escotistas de alcateia filtra apenas `ativo=true`

### 5.4 Atribuições de Atividade
- Status padrão: `pendente`
- Campos de validação (`validadoPor`, `validadoEm`, `feedback`) são opcionais e preenchidos pelo validador via PUT
- Array `midiaUrl` (URLs de evidência) suporta múltiplos links

### 5.5 Ficha Médica
- Constraint UNIQUE em `associado_id` — somente uma ficha por associado
- POST realiza upsert (`INSERT ON CONFLICT DO UPDATE`)

### 5.6 Progressão
- Calculada automaticamente por trigger no banco (`fn_atualizar_progressao`)
- Disparado quando `atribuicoes_atividade` é inserida/atualizada
- Não há endpoint de escrita para progressão

### 5.7 Associado — campos imutáveis via PUT
`matricula`, `perfil`, `cpf`, `rg`, `passaporte` **não podem ser alterados** via PUT — o UPDATE SQL não inclui esses campos.

---

## 6. FUNCIONALIDADES INCOMPLETAS OU PARCIALMENTE IMPLEMENTADAS

### 6.1 🔴 CRÍTICO — AuthFilter não valida JWT (bug de arquitetura)

**Problema:** O `AuthFilter` usa `TokenStore.isValid(token)` para autenticar requisições. Porém, `TokenStore.register()` **nunca é chamado** pelo `LoginServlet` nem pelo `RefreshServlet`. O `TokenStore` fica sempre vazio, fazendo com que **todas as requisições autenticadas retornem 401**.

**Impacto:** A API está completamente inacessível após login.

**Causa:** O design misturou duas abordagens:
- `JwtUtil` para geração/validação criptográfica de tokens (correto, stateless)
- `TokenStore` para revogação (apenas necessário para logout)

**Correção necessária:** O `AuthFilter` deve usar `JwtUtil.isValido(token)` para validar o JWT, e `TokenStore` deve ser usado apenas para checar tokens revogados:

```java
// AuthFilter.doFilter — lógica correta:
if (!JwtUtil.isValido(token)) { unauthorized(...); return; }
if (TokenStore.isRevoked(token)) { unauthorized(...); return; }
```

E `LoginServlet` deve continuar como está (não precisa registrar no TokenStore).

---

### 6.2 🔴 CRÍTICO — NPE em LogoutServlet

**Arquivo:** `LogoutServlet.java:16`
```java
String token = req.getHeader("Authorization").substring(7).trim(); // NPE se null
```
Se o header estiver ausente, lança `NullPointerException`. O `AuthFilter` deveria barrar antes, mas o logout está na rota protegida e um token revogado passaria pelo filtro antes de chegar ao servlet.

---

### 6.3 🟡 MÉDIO — Roles incompletas no JWT

Apenas `dirigente` e `escotista` têm roles distintas. Os perfis `senior` e `pioneiro` são tratados como `lobinho`. Se o frontend usa a role para controlar acesso, usuários `senior`/`pioneiro` terão nível de permissão errado.

**Arquivo:** `LoginServlet.java:93-100`, `RefreshServlet.java:104-111`

---

### 6.4 🟡 MÉDIO — Sem controle de autorização por perfil

Qualquer associado autenticado pode acessar qualquer endpoint. Não há verificação de role no `AuthFilter` nem nos Servlets. Um `lobinho` pode criar grupos, deletar associados, etc.

---

### 6.5 🟡 MÉDIO — DocumentoServlet: PUT implementado mas não documentado

O método `doPut` está implementado no `DocumentoServlet`, mas o JavaDoc da classe lista apenas GET, POST e DELETE. É uma funcionalidade existente não documentada.

---

### 6.6 🟡 MÉDIO — Sem paginação em nenhum endpoint

Todos os endpoints `GET` de coleção retornam todos os registros da tabela. Em produção com volumes de dados maiores isso causa problemas de performance e memória.

---

### 6.7 🟡 MÉDIO — Sem validação de dados de entrada

Nenhum campo é validado antes de ir ao banco. Exemplos:
- `perfil` pode receber um valor fora do enum e causará erro SQL sem mensagem clara
- `tipoSanguineo` em FichaMedica não é validado
- `dataNascimento` pode receber formato inválido
- UUIDs inválidos causam `IllegalArgumentException` não tratada de forma específica

---

### 6.8 🟡 MÉDIO — FichaMedica não tem PUT dedicado

O endpoint `POST /api/fichas-medicas` faz upsert, mas não há `PUT /api/fichas-medicas/{id}` para atualizar por ID direto.

---

### 6.9 🟡 MÉDIO — Não há endpoint para alterar senha

Não existe rota para o associado alterar sua própria senha. `PUT /api/associados/{id}` não inclui o campo `senha` no UPDATE SQL.

---

### 6.10 🟢 MENOR — BaseServlet.erro() monta JSON por concatenação

**Arquivo:** `BaseServlet.java:52`
```java
res.getWriter().print("{\"erro\":\"" + mensagem + "\"}");
```
Se `mensagem` contiver aspas duplas ou caracteres especiais, o JSON resultante será inválido.

---

### 6.11 🟢 MENOR — TokenStore.TTL fixo em 8 horas, divergente do JWT

O `TokenStore` usa TTL de 8 horas fixas. O `accessToken` JWT expira em 30 minutos. Há inconsistência entre os dois valores, mas como o TokenStore não é usado atualmente para validar (ver item 6.1), o impacto é nulo até a correção do bug.

---

### 6.12 🟢 MENOR — Sem endpoint para listar `EscotistaAlcateia` por ID direto

`GET /api/escotistas-alcateias/{id}` **não funciona** pois o DAO tem `buscarPorId()` mas o Servlet não trata o caso de `extrairId() != null` no `doGet()` — vai cair no bloco de `alcateiaParam == null` e retornar erro 400.

---

## 7. DADOS QUE O FRONTEND PRECISA ENVIAR — RESUMO POR FLUXO

### Fluxo de Login
```json
POST /api/auth/login
{ "matricula": "...", "senha": "..." }
```

### Fluxo de Cadastro de Associado
```json
POST /api/associados
{
  "grupoEscoteiroId": "uuid",
  "matricula": "...",
  "senha": "...",
  "perfil": "lobinho|escoteiro|senior|pioneiro|escotista|dirigente",
  "nomeCompleto": "..."
  // + campos opcionais conforme necessidade
}
```

### Fluxo de Ficha Médica
```json
POST /api/fichas-medicas
{
  "associadoId": "uuid",
  "tipoSanguineo": "A_POSITIVO",
  "alergias": ["Amendoim"],
  "condicoes": ["Asma"],
  "medicamentos": ["Salbutamol 100mcg"],
  "observacoes": "..."
}
```

### Fluxo de Atribuição e Conclusão de Atividade
```json
// 1. Criar atribuição
POST /api/atribuicoes
{ "atividadeId": "uuid", "associadoId": "uuid" }

// 2. Registrar conclusão (pelo associado)
PUT /api/atribuicoes/{id}
{
  "status": "concluida",
  "descricaoRegistro": "Concluí a atividade ...",
  "midiaUrl": ["https://...foto1.jpg", "https://...foto2.jpg"]
}

// 3. Validar (pelo escotista/dirigente)
PUT /api/atribuicoes/{id}
{
  "status": "concluida",
  "validadoPor": "uuid-do-escotista",
  "validadoEm": "2026-05-29T10:00:00Z",
  "feedback": "Excelente trabalho!"
}
```

### Fluxo de Vínculo Escotista–Alcateia
```json
// Vincular
POST /api/escotistas-alcateias
{ "associadoId": "uuid", "alcateiaId": "uuid" }

// Encerrar vínculo
PUT /api/escotistas-alcateias/{id}
// sem body — seta ativo=false e data_fim=hoje
```

### Fluxo de Adição de Membro à Matilha
```json
// Adicionar
POST /api/membros-matilha
{ "matilhaId": "uuid", "associadoId": "uuid", "cargo": "membro|lider|sublider" }

// Encerrar participação
PUT /api/membros-matilha/{id}
// sem body — seta ativo=false e data_saida=hoje
```

---

## 8. RESUMO DE PROBLEMAS POR PRIORIDADE

| # | Prioridade | Problema | Arquivo |
|---|---|---|---|
| 1 | 🔴 CRÍTICO | AuthFilter usa TokenStore (sempre vazio) — todas as rotas protegidas retornam 401 | `AuthFilter.java` |
| 2 | 🔴 CRÍTICO | NPE em LogoutServlet sem verificação de header | `LogoutServlet.java:16` |
| 3 | 🟡 MÉDIO | Roles `senior`/`pioneiro` mapeiam para `lobinho` incorretamente | `LoginServlet.java:93` |
| 4 | 🟡 MÉDIO | Sem autorização por perfil — qualquer usuário acessa tudo | `AuthFilter.java` |
| 5 | 🟡 MÉDIO | Sem validação de dados de entrada | Todos os Servlets |
| 6 | 🟡 MÉDIO | Sem endpoint para alterar senha | `AssociadoServlet.java` |
| 7 | 🟡 MÉDIO | `GET /api/escotistas-alcateias/{id}` não funciona | `EscotistaAlcateiaServlet.java` |
| 8 | 🟡 MÉDIO | Sem paginação em endpoints de coleção | Todos os Servlets |
| 9 | 🟢 MENOR | `BaseServlet.erro()` monta JSON por concatenação (risco de JSON inválido) | `BaseServlet.java:52` |
| 10 | 🟢 MENOR | TokenStore TTL (8h) diverge do JWT accessToken (30 min) | `TokenStore.java` |
| 11 | 🟢 MENOR | CORS `Allow-Origin: *` — permissivo demais para produção | `CorsFilter.java` |
| 12 | 🟢 MENOR | Secret JWT padrão hardcoded no código | `JwtConfig.java:11` |
| 13 | 🟢 MENOR | Sem pool de conexões (HikariCP) — nova conexão a cada operação | Todos os DAOs |

---

## 9. DEPENDÊNCIAS (pom.xml)

| Artefato | Versão | Uso |
|---|---|---|
| `jakarta.servlet-api` | 6.0.0 | Servlet API (Jakarta EE 10) |
| `postgresql` | 42.7.3 | Driver JDBC PostgreSQL |
| `gson` | 2.10.1 | Serialização JSON |
| `dotenv-java` | 3.0.0 | Leitura de `.env` |
| `jbcrypt` | 0.4 | Hash BCrypt para senhas |
| `jjwt-api` | 0.12.6 | API JWT |
| `jjwt-impl` | 0.12.6 | Implementação JWT (runtime) |
| `jjwt-jackson` | 0.12.6 | Serialização JWT com Jackson (runtime) |

---

*Fim do documento — BACKEND_ANALYSIS.md*
