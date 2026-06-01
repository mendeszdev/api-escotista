-- ============================================================
-- MIGRATION: Neon PostgreSQL
-- Execute completo no SQL Editor do Neon
-- ============================================================

-- Extensões
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- TIPOS CUSTOMIZADOS (ENUMs)
-- ============================================================

CREATE TYPE acao_tipo AS ENUM ('fixa', 'variavel');

CREATE TYPE atividade_status AS ENUM (
  'pendente', 'aprovada', 'rejeitada', 'aprovado', 'rejeitado', 'recusada'
);

CREATE TYPE cargo_matilha AS ENUM ('membro', 'primo', 'segundo_primo');

CREATE TYPE eixo_tipo AS ENUM (
  'habilidades_para_vida',
  'meio_ambiente',
  'paz_e_desenvolvimento',
  'saude_e_bem_estar',
  'habilidades_vida'
);

CREATE TYPE perfil_tipo AS ENUM ('lobinho', 'escotista', 'dirigente');

CREATE TYPE status_geral AS ENUM ('ativo', 'inativo', 'pendente');

CREATE TYPE tipo_sanguineo AS ENUM (
  'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'
);

-- ============================================================
-- TABELAS (ordem respeita dependências de FK)
-- ============================================================

CREATE TABLE grupos_escoteiros (
  id                uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  nome              character varying(120) NOT NULL,
  numero            character varying(20),
  distrito          character varying(100),
  regiao            character varying(100),
  paleta_cores      jsonb,
  logo_url          text,
  status            status_geral NOT NULL DEFAULT 'ativo',
  criado_em         timestamptz  NOT NULL DEFAULT now(),
  atualizado_em     timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE alcateias (
  id                uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  grupo_escoteiro_id uuid NOT NULL REFERENCES grupos_escoteiros(id) ON DELETE CASCADE,
  nome              character varying(120) NOT NULL,
  descricao         text,
  status            status_geral NOT NULL DEFAULT 'ativo',
  criado_em         timestamptz  NOT NULL DEFAULT now(),
  atualizado_em     timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE associados (
  id                   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  grupo_escoteiro_id   uuid NOT NULL REFERENCES grupos_escoteiros(id) ON DELETE CASCADE,
  matricula            character varying(30)  NOT NULL UNIQUE,
  senha_hash           text                   NOT NULL,
  perfil               perfil_tipo            NOT NULL,
  nome_completo        character varying(200) NOT NULL,
  nome_escoteiro       character varying(100),
  data_nascimento      date                   NOT NULL,
  genero               character varying(40),
  estado_civil         character varying(40),
  cpf                  character varying(14),
  rg                   character varying(30),
  passaporte           character varying(30),
  email                character varying(200),
  telefone             character varying(20),
  cep                  character varying(10),
  logradouro           text,
  numero_end           character varying(20),
  bairro               character varying(100),
  cidade               character varying(100),
  estado               character(2),
  foto_url             text,
  status               status_geral NOT NULL DEFAULT 'ativo',
  documentos_validados boolean      NOT NULL DEFAULT false,
  criado_em            timestamptz  NOT NULL DEFAULT now(),
  atualizado_em        timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE eixos (
  id        uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  tipo      eixo_tipo          NOT NULL,
  nome      character varying(100) NOT NULL,
  descricao text,
  icone     character varying(10),
  cor_hex   character varying(7),
  ordem     smallint NOT NULL DEFAULT 0
);

CREATE TABLE blocos_aprendizagem (
  id                   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  eixo_id              uuid NOT NULL REFERENCES eixos(id) ON DELETE CASCADE,
  nome                 character varying(120) NOT NULL,
  descricao            text,
  min_acoes_variaveis  smallint NOT NULL DEFAULT 1,
  ordem                smallint NOT NULL DEFAULT 0,
  ativo                boolean  NOT NULL DEFAULT true,
  intencionalidade     text
);

CREATE TABLE acoes_educativas (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  bloco_id     uuid NOT NULL REFERENCES blocos_aprendizagem(id) ON DELETE CASCADE,
  nome         character varying(180) NOT NULL,
  descricao    text,
  tipo         acao_tipo NOT NULL,
  e_especialidade boolean NOT NULL DEFAULT false,
  criado_por   uuid REFERENCES associados(id) ON DELETE SET NULL,
  ativo        boolean     NOT NULL DEFAULT true,
  criado_em    timestamptz NOT NULL DEFAULT now(),
  atualizado_em timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE atividades (
  id                     uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  acao_educativa_id      uuid REFERENCES acoes_educativas(id) ON DELETE SET NULL,
  criado_por             uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  alcateia_id            uuid NOT NULL REFERENCES alcateias(id) ON DELETE CASCADE,
  e_personalizada        boolean NOT NULL DEFAULT false,
  nome_personalizado     character varying(180),
  descricao_personalizada text,
  aprovada_por           uuid REFERENCES associados(id) ON DELETE SET NULL,
  data_limite            date,
  criado_em              timestamptz NOT NULL DEFAULT now(),
  atualizado_em          timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE atribuicoes_atividade (
  id                  uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  atividade_id        uuid NOT NULL REFERENCES atividades(id) ON DELETE CASCADE,
  associado_id        uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  status              atividade_status NOT NULL DEFAULT 'pendente',
  registrado_em       timestamptz,
  descricao_registro  text,
  midia_url           text[],
  validado_por        uuid REFERENCES associados(id) ON DELETE SET NULL,
  validado_em         timestamptz,
  feedback            text,
  data_realizacao     date
);

CREATE TABLE matilhas (
  id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  alcateia_id uuid NOT NULL REFERENCES alcateias(id) ON DELETE CASCADE,
  nome        character varying(120) NOT NULL,
  cor         character varying(40),
  icone_url   text,
  status      status_geral NOT NULL DEFAULT 'ativo',
  criado_em   timestamptz  NOT NULL DEFAULT now(),
  atualizado_em timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE membros_matilha (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  matilha_id   uuid NOT NULL REFERENCES matilhas(id) ON DELETE CASCADE,
  associado_id uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  cargo        cargo_matilha NOT NULL DEFAULT 'membro',
  data_entrada date NOT NULL DEFAULT CURRENT_DATE,
  data_saida   date,
  ativo        boolean NOT NULL DEFAULT true
);

CREATE TABLE escotistas_alcateias (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  alcateia_id  uuid NOT NULL REFERENCES alcateias(id) ON DELETE CASCADE,
  data_inicio  date NOT NULL DEFAULT CURRENT_DATE,
  data_fim     date,
  ativo        boolean NOT NULL DEFAULT true
);

CREATE TABLE progressoes (
  id                  uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id        uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  eixo_id             uuid NOT NULL REFERENCES eixos(id) ON DELETE CASCADE,
  percentual_concluido numeric(5,2) NOT NULL DEFAULT 0.00,
  atualizado_em       timestamptz NOT NULL DEFAULT now(),
  UNIQUE (associado_id, eixo_id)
);

CREATE TABLE distintivos (
  id             uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  nome           character varying(120) NOT NULL,
  descricao      text,
  icone          character varying(10),
  cor_hex        character varying(7),
  e_especialidade boolean NOT NULL DEFAULT false,
  ativo          boolean     NOT NULL DEFAULT true,
  criado_em      timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE associado_distintivos (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  associado_id   uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  distintivo_id  uuid NOT NULL REFERENCES distintivos(id) ON DELETE CASCADE,
  atribuido_por  uuid REFERENCES associados(id) ON DELETE SET NULL,
  atribuido_em   timestamptz NOT NULL DEFAULT now(),
  observacao     text,
  UNIQUE (associado_id, distintivo_id)
);

CREATE TABLE documentos (
  id             uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id   uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  tipo           character varying(80)  NOT NULL,
  nome_arquivo   character varying(255) NOT NULL,
  url            text NOT NULL,
  tamanho_bytes  bigint,
  validade       date,
  obrigatorio    boolean     NOT NULL DEFAULT false,
  status         status_geral NOT NULL DEFAULT 'ativo',
  criado_em      timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE fichas_medicas (
  id             uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id   uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  tipo_sanguineo tipo_sanguineo,
  alergias       text[],
  condicoes      text[],
  medicamentos   text[],
  observacoes    text,
  criado_em      timestamptz NOT NULL DEFAULT now(),
  atualizado_em  timestamptz NOT NULL DEFAULT now(),
  UNIQUE (associado_id)
);

CREATE TABLE responsaveis_legais (
  id                    uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id          uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  nome_completo         character varying(200) NOT NULL,
  parentesco            character varying(60),
  cpf                   character varying(14),
  telefone              character varying(20) NOT NULL,
  email                 character varying(200),
  responsavel_financeiro boolean NOT NULL DEFAULT false,
  criado_em             timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE etapas_progressao (
  id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  nome        character varying(80) NOT NULL,
  descricao   text,
  icone       character varying(10),
  ordem       smallint NOT NULL,
  requisitos  jsonb
);

CREATE TABLE lobinho_etapa (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  etapa_id     uuid NOT NULL REFERENCES etapas_progressao(id) ON DELETE CASCADE,
  atingida_em  timestamptz NOT NULL DEFAULT now(),
  atualizado_em timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE historico_lobinho (
  id             uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id   uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  tipo_evento    character varying(60) NOT NULL,
  referencia_id  uuid,
  descricao      text,
  ocorrido_em    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE formacoes_escoteiras (
  id              uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id    uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  nivel           character varying(100) NOT NULL,
  instituicao     character varying(150),
  carga_horaria   integer,
  data_conclusao  date,
  certificado_url text,
  criado_em       timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE formacoes_dirigente (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  associado_id  uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  nivel         character varying(150) NOT NULL,
  descricao     text,
  concluido_em  date,
  carga_horaria integer NOT NULL DEFAULT 0,
  criado_em     timestamptz NOT NULL DEFAULT now(),
  atualizado_em timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE funcoes_exercidas (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  associado_id uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  alcateia_id  uuid REFERENCES alcateias(id) ON DELETE SET NULL,
  cargo        character varying(120) NOT NULL,
  data_inicio  date NOT NULL,
  data_fim     date,
  status       status_geral NOT NULL DEFAULT 'ativo',
  criado_em    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE configuracoes_sistema (
  id                      uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  grupo_escoteiro_id      uuid NOT NULL REFERENCES grupos_escoteiros(id) ON DELETE CASCADE,
  email_semanal_ativo     boolean NOT NULL DEFAULT true,
  notif_push_ativo        boolean NOT NULL DEFAULT false,
  alertas_matricula_ativo boolean NOT NULL DEFAULT true,
  ultimo_backup           timestamptz,
  criado_em               timestamptz NOT NULL DEFAULT now(),
  atualizado_em           timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE grupos_atividade (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  atividade_id uuid NOT NULL REFERENCES atividades(id) ON DELETE CASCADE,
  nome         character varying(120),
  criado_por   uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
  criado_em    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE grupos_atividade_membros (
  id                uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  grupo_atividade_id uuid NOT NULL REFERENCES grupos_atividade(id) ON DELETE CASCADE,
  associado_id      uuid NOT NULL REFERENCES associados(id) ON DELETE CASCADE
);

-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_associados_perfil       ON associados(perfil);
CREATE INDEX idx_associados_grupo        ON associados(grupo_escoteiro_id);
CREATE INDEX idx_atribuicoes_associado   ON atribuicoes_atividade(associado_id);
CREATE INDEX idx_atribuicoes_atividade   ON atribuicoes_atividade(atividade_id);
CREATE INDEX idx_atribuicoes_status      ON atribuicoes_atividade(status);
CREATE INDEX idx_membros_matilha         ON membros_matilha(matilha_id);
CREATE INDEX idx_membros_associado       ON membros_matilha(associado_id);
CREATE INDEX idx_progressoes_associado   ON progressoes(associado_id);
CREATE INDEX idx_blocos_eixo             ON blocos_aprendizagem(eixo_id);
CREATE INDEX idx_acoes_bloco             ON acoes_educativas(bloco_id);
CREATE INDEX idx_atividades_alcateia     ON atividades(alcateia_id);
CREATE INDEX idx_assoc_dist_associado    ON associado_distintivos(associado_id);
CREATE INDEX idx_escotistas_alcateia     ON escotistas_alcateias(alcateia_id);
CREATE INDEX idx_escotistas_associado    ON escotistas_alcateias(associado_id);
