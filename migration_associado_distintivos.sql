-- ============================================================
-- MIGRATION: Tabela associado_distintivos
-- Execute no SQL Editor do Supabase.
-- ============================================================

CREATE TABLE IF NOT EXISTS associado_distintivos (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    associado_id  UUID NOT NULL REFERENCES associados(id) ON DELETE CASCADE,
    distintivo_id UUID NOT NULL REFERENCES distintivos(id) ON DELETE CASCADE,
    observacao    TEXT,
    atribuido_por UUID REFERENCES associados(id) ON DELETE SET NULL,
    atribuido_em  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_associado_distintivo UNIQUE (associado_id, distintivo_id)
);

CREATE INDEX IF NOT EXISTS idx_assoc_dist_associado  ON associado_distintivos(associado_id);
CREATE INDEX IF NOT EXISTS idx_assoc_dist_distintivo ON associado_distintivos(distintivo_id);
