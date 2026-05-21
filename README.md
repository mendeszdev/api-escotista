# 🏕️ Escoteiros API — Java JSP + Supabase

API REST desenvolvida em Java com Servlets/JSP conectada ao PostgreSQL via Supabase.

---

## ⚙️ Pré-requisitos

| Ferramenta    | Versão mínima | Download                        |
|---------------|---------------|---------------------------------|
| JDK           | 17+           | adoptium.net                    |
| Apache Tomcat | 10+           | tomcat.apache.org               |
| Maven         | 3.8+          | (IntelliJ baixa automaticamente)|
| IntelliJ IDEA | 2022+         | jetbrains.com/idea              |

---

## 🚀 Setup local (passo a passo)

### 1. Clonar o projeto
```bash
git clone https://github.com/seu-usuario/escoteiros-api.git
cd escoteiros-api
```

### 2. Configurar variáveis de ambiente
```bash
cp .env.example .env
```
Abra o `.env` e preencha com suas credenciais do Supabase:
```env
DB_HOST=zatupnilwsnoqfmlqzte.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASS=sua-senha-aqui
```

> ⚠️ O arquivo `.env` está no `.gitignore` e **nunca** será enviado ao GitHub.

### 3. Abrir no IntelliJ
- `File → Open` → selecione a pasta do projeto
- Aguarde o Maven baixar todas as dependências (barra de progresso no rodapé)

### 4. Configurar o Tomcat
```
Run → Edit Configurations → + → Tomcat Server → Local
  ├── Server tab:
  │     Application server → Configure → pasta do Tomcat (ex: C:\tomcat10)
  │     HTTP port: 8080
  └── Deployment tab:
        + → Artifact → escoteiros-api:war exploded
        Application context: /
```

### 5. Rodar
Clique em **▶ Run** — acesse `http://localhost:8080`

---

## 📂 Estrutura de Pastas

```
escoteiros-api/
├── .env                    ← credenciais locais (NÃO vai pro Git)
├── .env.example            ← modelo público (VAI pro Git)
├── .gitignore
├── pom.xml
└── src/main/
    ├── java/com/escoteiros/
    │   ├── config/
    │   │   ├── DatabaseConfig.java   ← lê .env e conecta ao Supabase
    │   │   └── GsonConfig.java       ← serialização JSON
    │   ├── filter/
    │   │   └── CorsFilter.java       ← CORS global
    │   ├── model/                    ← espelham as tabelas do banco
    │   │   ├── GrupoEscoteiro.java
    │   │   ├── Associado.java
    │   │   ├── Alcateia.java
    │   │   ├── Matilha.java
    │   │   ├── AcaoEducativa.java
    │   │   ├── Atividade.java
    │   │   ├── AtribuicaoAtividade.java
    │   │   ├── Distintivo.java
    │   │   ├── Documento.java
    │   │   ├── FichaMedica.java
    │   │   ├── Progressao.java
    │   │   └── ResponsavelLegal.java
    │   ├── dao/                      ← SQL JDBC (CRUD)
    │   │   ├── GrupoEscotelroDAO.java
    │   │   ├── AssociadoDAO.java
    │   │   ├── AlcateiaDAO.java
    │   │   ├── MatilhaDAO.java
    │   │   ├── AcaoEducativaDAO.java
    │   │   ├── AtividadeDAO.java
    │   │   ├── AtribuicaoAtividadeDAO.java
    │   │   ├── DistintivoDAO.java
    │   │   ├── DocumentoDAO.java
    │   │   ├── FichaMedicaDAO.java
    │   │   ├── ProgressaoDAO.java
    │   │   └── ResponsavelLegalDAO.java
    │   ├── servlet/                  ← rotas HTTP
    │   │   ├── GrupoEscotelroServlet.java
    │   │   ├── AssociadoServlet.java
    │   │   ├── AlcateiaServlet.java
    │   │   ├── MatilhaServlet.java
    │   │   ├── AcaoEducativaServlet.java
    │   │   ├── AtividadeServlet.java
    │   │   ├── AtribuicaoAtividadeServlet.java
    │   │   ├── DistintivoServlet.java
    │   │   ├── DocumentoServlet.java
    │   │   ├── FichaMedicaServlet.java
    │   │   ├── ProgressaoServlet.java
    │   │   └── ResponsavelLegalServlet.java
    │   └── util/
    │       └── BaseServlet.java      ← helpers: json, lerBody, erro, ok
    └── webapp/
        ├── WEB-INF/web.xml
        └── index.jsp
```

---

## 🌐 Endpoints

### Grupos Escoteiros `/api/grupos-escoteiros`
| Método | URL                             | Ação          |
|--------|---------------------------------|---------------|
| GET    | /api/grupos-escoteiros          | Listar todos  |
| GET    | /api/grupos-escoteiros/{id}     | Buscar por ID |
| POST   | /api/grupos-escoteiros          | Criar         |
| PUT    | /api/grupos-escoteiros/{id}     | Atualizar     |
| DELETE | /api/grupos-escoteiros/{id}     | Remover       |

### Associados `/api/associados`
| Método | URL                                  | Ação                 |
|--------|--------------------------------------|----------------------|
| GET    | /api/associados                      | Listar todos         |
| GET    | /api/associados/{id}                 | Buscar por ID        |
| GET    | /api/associados?grupo={uuid}         | Filtrar por grupo    |
| GET    | /api/associados?perfil={perfil_tipo} | Filtrar por perfil   |
| POST   | /api/associados                      | Criar                |
| PUT    | /api/associados/{id}                 | Atualizar            |
| DELETE | /api/associados/{id}                 | Remover              |

### Alcateias `/api/alcateias`
| Método | URL                            | Ação                 |
|--------|--------------------------------|----------------------|
| GET    | /api/alcateias                 | Listar todas         |
| GET    | /api/alcateias/{id}            | Buscar por ID        |
| GET    | /api/alcateias?grupo={uuid}    | Filtrar por grupo    |
| POST   | /api/alcateias                 | Criar                |
| PUT    | /api/alcateias/{id}            | Atualizar            |
| DELETE | /api/alcateias/{id}            | Remover              |

### Matilhas `/api/matilhas`
| Método | URL                               | Ação                  |
|--------|-----------------------------------|-----------------------|
| GET    | /api/matilhas                     | Listar todas          |
| GET    | /api/matilhas/{id}                | Buscar por ID         |
| GET    | /api/matilhas?alcateia={uuid}     | Filtrar por alcateia  |
| POST   | /api/matilhas                     | Criar                 |
| PUT    | /api/matilhas/{id}                | Atualizar             |
| DELETE | /api/matilhas/{id}                | Remover               |

### Ações Educativas `/api/acoes-educativas`
| Método | URL                                 | Ação               |
|--------|-------------------------------------|--------------------|
| GET    | /api/acoes-educativas               | Listar todas       |
| GET    | /api/acoes-educativas/{id}          | Buscar por ID      |
| GET    | /api/acoes-educativas?bloco={uuid}  | Filtrar por bloco  |
| POST   | /api/acoes-educativas               | Criar              |
| PUT    | /api/acoes-educativas/{id}          | Atualizar          |
| DELETE | /api/acoes-educativas/{id}          | Remover            |

### Atividades `/api/atividades`
| Método | URL                                 | Ação                  |
|--------|-------------------------------------|-----------------------|
| GET    | /api/atividades                     | Listar todas          |
| GET    | /api/atividades/{id}                | Buscar por ID         |
| GET    | /api/atividades?alcateia={uuid}     | Filtrar por alcateia  |
| POST   | /api/atividades                     | Criar                 |
| PUT    | /api/atividades/{id}                | Atualizar             |
| DELETE | /api/atividades/{id}                | Remover               |

### Atribuições `/api/atribuicoes`
| Método | URL                                    | Ação                    |
|--------|----------------------------------------|-------------------------|
| GET    | /api/atribuicoes                       | Listar todas            |
| GET    | /api/atribuicoes/{id}                  | Buscar por ID           |
| GET    | /api/atribuicoes?associado={uuid}      | Filtrar por associado   |
| GET    | /api/atribuicoes?atividade={uuid}      | Filtrar por atividade   |
| POST   | /api/atribuicoes                       | Criar                   |
| PUT    | /api/atribuicoes/{id}                  | Atualizar               |
| DELETE | /api/atribuicoes/{id}                  | Remover                 |

### Distintivos `/api/distintivos`
| Método | URL                       | Ação          |
|--------|---------------------------|---------------|
| GET    | /api/distintivos          | Listar todos  |
| GET    | /api/distintivos/{id}     | Buscar por ID |
| POST   | /api/distintivos          | Criar         |
| PUT    | /api/distintivos/{id}     | Atualizar     |
| DELETE | /api/distintivos/{id}     | Remover       |

### Documentos `/api/documentos`
| Método | URL                                  | Ação                 |
|--------|--------------------------------------|----------------------|
| GET    | /api/documentos/{id}                 | Buscar por ID        |
| GET    | /api/documentos?associado={uuid}     | Listar por associado |
| POST   | /api/documentos                      | Criar                |
| DELETE | /api/documentos/{id}                 | Remover              |

### Fichas Médicas `/api/fichas-medicas`
| Método | URL                                       | Ação               |
|--------|-------------------------------------------|--------------------|
| GET    | /api/fichas-medicas?associado={uuid}      | Buscar ficha       |
| POST   | /api/fichas-medicas                       | Criar ou atualizar |

### Progressões `/api/progressoes`
| Método | URL                                      | Ação                    |
|--------|------------------------------------------|-------------------------|
| GET    | /api/progressoes                         | Listar todas            |
| GET    | /api/progressoes?associado={uuid}        | Progressão do associado |

> ℹ️ Calculadas automaticamente pelo trigger `fn_atualizar_progressao`.

### Responsáveis Legais `/api/responsaveis`
| Método | URL                                    | Ação                 |
|--------|----------------------------------------|----------------------|
| GET    | /api/responsaveis/{id}                 | Buscar por ID        |
| GET    | /api/responsaveis?associado={uuid}     | Listar por associado |
| POST   | /api/responsaveis                      | Criar                |
| PUT    | /api/responsaveis/{id}                 | Atualizar            |
| DELETE | /api/responsaveis/{id}                 | Remover              |

---

## 📝 Exemplos de Requisição (Postman)

### Criar Grupo Escoteiro
```
POST http://localhost:8080/api/grupos-escoteiros
Content-Type: application/json

{
  "nome": "Grupo Escoteiro Araucária",
  "numero": "42",
  "distrito": "Bahia",
  "regiao": "Nordeste",
  "status": "ativo"
}
```

### Criar Associado
```
POST http://localhost:8080/api/associados
Content-Type: application/json

{
  "grupoEscotelroId": "uuid-do-grupo",
  "matricula": "2024001",
  "senhaHash": "senha123",
  "perfil": "lobinho",
  "nomeCompleto": "João da Silva",
  "dataNascimento": "2015-03-10",
  "email": "joao@exemplo.com",
  "status": "ativo"
}
```

### Criar/Atualizar Ficha Médica (upsert)
```
POST http://localhost:8080/api/fichas-medicas
Content-Type: application/json

{
  "associadoId": "uuid-do-associado",
  "tipoSanguineo": "O_POSITIVO",
  "alergias": ["Amendoim", "Látex"],
  "condicoes": ["Asma leve"],
  "medicamentos": [],
  "observacoes": "Usar bombinha em caso de crise"
}
```

---

## 🛡️ Enums do banco (valores aceitos)

| Enum               | Valores válidos                                                    |
|--------------------|--------------------------------------------------------------------|
| `perfil_tipo`      | `lobinho`, `escoteiro`, `senior`, `pioneiro`, `escotista`, `dirigente` |
| `status_geral`     | `ativo`, `inativo`, `suspenso`                                     |
| `atividade_status` | `pendente`, `em_andamento`, `concluida`, `cancelada`               |
| `cargo_matilha`    | `membro`, `lider`, `sublider`                                      |
| `tipo_sanguineo`   | `A_POSITIVO`, `A_NEGATIVO`, `B_POSITIVO`, `B_NEGATIVO`, `AB_POSITIVO`, `AB_NEGATIVO`, `O_POSITIVO`, `O_NEGATIVO` |

---

## 🚢 Subindo para o GitHub

```bash
# 1. Inicializar repositório (se ainda não tiver)
git init
git remote add origin https://github.com/seu-usuario/escoteiros-api.git

# 2. Verificar que o .env NÃO está sendo rastreado
git status
# .env não deve aparecer na lista

# 3. Commitar e enviar
git add .
git commit -m "feat: projeto inicial escoteiros-api"
git push -u origin main
```

> ✅ O `.gitignore` já garante que `.env` nunca seja enviado.
> ✅ O `.env.example` vai junto como guia para outros devs.

---

## 🔒 Segurança para produção

- Nunca commite `.env` no Git
- Use `System.getenv("DB_PASS")` se hospedar em servidor com variáveis de sistema
- Implemente autenticação JWT para proteger os endpoints
- Restrinja `Access-Control-Allow-Origin` ao domínio do frontend
- Considere BCrypt para hash de senhas (`org.mindrot:jbcrypt:0.4`)
