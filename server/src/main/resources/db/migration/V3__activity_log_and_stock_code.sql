ALTER TABLE stock_items ADD COLUMN IF NOT EXISTS code TEXT;

CREATE TABLE IF NOT EXISTS activity_logs (
    id TEXT PRIMARY KEY,
    farm_id TEXT NOT NULL,
    type TEXT NOT NULL,
    action TEXT NOT NULL,
    entity_id TEXT,
    occurred_at TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    subject_name TEXT NOT NULL DEFAULT '',
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_activity_logs_farm_time
    ON activity_logs (farm_id, occurred_at DESC);

INSERT INTO activity_logs
    (id, farm_id, type, action, entity_id, occurred_at, title, description, subject_name, version, updated_at)
SELECT 'import-event-' || id, farm_id, 'ANIMAL', 'IMPORTADO', animal_id,
       date || CASE WHEN time = '' THEN 'T00:00:00Z' ELSE 'T' || time || ':00Z' END,
       'Evento importado', notes, '', 1, updated_at
FROM animal_events
WHERE deleted_at IS NULL
ON CONFLICT (id) DO NOTHING;

INSERT INTO activity_logs
    (id, farm_id, type, action, entity_id, occurred_at, title, description, subject_name, version, updated_at)
SELECT 'import-animal-' || id, farm_id, 'ANIMAL', 'IMPORTADO', id, updated_at,
       'Animal importado', tag_number, name, 1, updated_at
FROM animals WHERE deleted_at IS NULL AND updated_at <> ''
ON CONFLICT (id) DO NOTHING;

INSERT INTO activity_logs
    (id, farm_id, type, action, entity_id, occurred_at, title, description, subject_name, version, updated_at)
SELECT 'import-stock-' || id, farm_id, 'ESTOQUE', 'IMPORTADO', id, updated_at,
       'Item de estoque importado', quantity::text || ' ' || unit, name, 1, updated_at
FROM stock_items WHERE deleted_at IS NULL AND updated_at <> ''
ON CONFLICT (id) DO NOTHING;

INSERT INTO activity_logs
    (id, farm_id, type, action, entity_id, occurred_at, title, description, subject_name, version, updated_at)
SELECT 'import-group-' || id, farm_id, 'GRUPO', 'IMPORTADO', id, updated_at,
       'Grupo importado', description, name, 1, updated_at
FROM animal_groups WHERE deleted_at IS NULL AND updated_at <> ''
ON CONFLICT (id) DO NOTHING;

INSERT INTO activity_logs
    (id, farm_id, type, action, entity_id, occurred_at, title, description, subject_name, version, updated_at)
SELECT 'import-gestation-' || id, farm_id, 'GESTACAO', 'IMPORTADO', animal_id, updated_at,
       'Gestação importada', expected_birth_date, '', 1, updated_at
FROM gestations WHERE deleted_at IS NULL AND updated_at <> ''
ON CONFLICT (id) DO NOTHING;

INSERT INTO activity_logs
    (id, farm_id, type, action, entity_id, occurred_at, title, description, subject_name, version, updated_at)
SELECT 'import-production-' || id, farm_id, 'PRODUCAO', 'IMPORTADO', id,
       date || 'T00:00:00Z', 'Produção importada', quantity::text || ' ' || unit,
       product_type, 1, updated_at
FROM producao_entries
WHERE deleted_at IS NULL
ON CONFLICT (id) DO NOTHING;
