-- Tabela para armazenamento de arquivos binários (fotos de perfil, documentos)
-- Execute no console do Neon antes de usar os endpoints de upload.

CREATE TABLE IF NOT EXISTS arquivos (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    nome          VARCHAR(255)  NOT NULL,
    tipo_mime     VARCHAR(100),
    dados         BYTEA         NOT NULL,
    tamanho_bytes BIGINT,
    criado_em     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Índice opcional para limpeza futura por data
CREATE INDEX IF NOT EXISTS idx_arquivos_criado_em ON arquivos (criado_em);

-- Comentário de tamanho máximo recomendado: 15 MB por arquivo
-- Para arquivos maiores considere Object Storage externo (R2/S3).
