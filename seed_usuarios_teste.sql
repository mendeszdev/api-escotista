-- ============================================================
-- SEED: Usuários de Teste
-- Execute uma vez no banco Supabase (PostgreSQL).
-- Usa ON CONFLICT DO NOTHING para ser idempotente.
-- ATENÇÃO: senhas em texto puro — adequado apenas para DEV.
-- ============================================================

-- 1. Grupo Escoteiro de referência ─────────────────────────
INSERT INTO grupos_escoteiros (id, nome, numero, distrito, regiao, status)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Grupo Escoteiro Baden-Powell',
    '47/SP',
    'Capital',
    'São Paulo',
    'ativo'
)
ON CONFLICT (id) DO NOTHING;

-- 2. Usuário LOBINHO ────────────────────────────────────────
--    Acesso: matrícula = LB.2024-001 / senha = Lobinho@123
INSERT INTO associados (
    id, grupo_escoteiro_id, matricula, senha_hash, perfil,
    nome_completo, nome_escoteiro,
    data_nascimento, genero, estado_civil,
    cpf, rg,
    email, telefone,
    cep, logradouro, numero_end, bairro, cidade, estado,
    status, documentos_validados
) VALUES (
    'f1e2d3c4-b5a6-7890-abcd-1234567890ab',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'LB.2024-001',
    'Lobinho@123',
    'lobinho',
    'Pedro Henrique Santos Oliveira',
    'Falcão Veloz',
    '2015-03-15', 'M', 'solteiro',
    '123.456.789-09', '12.345.678-9',
    'pedro.santos.teste@email.com', '(11) 98765-4321',
    '01310-100', 'Avenida Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP',
    'ativo', false
)
ON CONFLICT (matricula) DO NOTHING;

-- 3. Usuário ESCOTISTA ──────────────────────────────────────
--    Acesso: matrícula = ES.2024-001 / senha = Escotista@123
INSERT INTO associados (
    id, grupo_escoteiro_id, matricula, senha_hash, perfil,
    nome_completo, nome_escoteiro,
    data_nascimento, genero, estado_civil,
    cpf, rg,
    email, telefone,
    cep, logradouro, numero_end, bairro, cidade, estado,
    status, documentos_validados
) VALUES (
    'c2d3e4f5-a6b7-8901-cdef-234567890abc',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'ES.2024-001',
    'Escotista@123',
    'escotista',
    'Ana Carolina Ferreira Lima',
    'Águia Dourada',
    '1995-07-22', 'F', 'solteiro',
    '987.654.321-00', '98.765.432-1',
    'ana.ferreira.teste@email.com', '(11) 97654-3210',
    '04538-133', 'Rua Funchal', '418', 'Vila Olímpia', 'São Paulo', 'SP',
    'ativo', true
)
ON CONFLICT (matricula) DO NOTHING;

-- 4. Usuário DIRIGENTE ──────────────────────────────────────
--    Acesso: matrícula = DG.2024-001 / senha = Dirigente@123
INSERT INTO associados (
    id, grupo_escoteiro_id, matricula, senha_hash, perfil,
    nome_completo, nome_escoteiro,
    data_nascimento, genero, estado_civil,
    cpf, rg,
    email, telefone,
    cep, logradouro, numero_end, bairro, cidade, estado,
    status, documentos_validados
) VALUES (
    'd3e4f5a6-b7c8-9012-def0-34567890abcd',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'DG.2024-001',
    'Dirigente@123',
    'dirigente',
    'Carlos Eduardo Mendes Costa',
    'Leão do Norte',
    '1978-11-05', 'M', 'casado',
    '456.789.012-34', '45.678.901-2',
    'carlos.mendes.teste@email.com', '(11) 96543-2109',
    '01414-000', 'Alameda Santos', '200', 'Cerqueira César', 'São Paulo', 'SP',
    'ativo', true
)
ON CONFLICT (matricula) DO NOTHING;

-- ============================================================
-- Verificação rápida após executar:
-- SELECT matricula, perfil, nome_completo, status FROM associados
-- WHERE matricula IN ('LB.2024-001','ES.2024-001','DG.2024-001');
-- ============================================================
