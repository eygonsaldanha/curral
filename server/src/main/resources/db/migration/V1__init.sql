-- Migração inicial: todas as tabelas do Curral

CREATE TABLE animals (
    id              TEXT PRIMARY KEY,
    farm_id         TEXT NOT NULL,
    name            TEXT NOT NULL,
    type            TEXT NOT NULL,
    breed           TEXT NOT NULL,
    status          TEXT NOT NULL,
    sex             TEXT NOT NULL,
    tag_number      TEXT NOT NULL,
    birth_date      TEXT NOT NULL,
    weight_kg       DOUBLE PRECISION NOT NULL,
    group_ids       TEXT NOT NULL DEFAULT '[]',
    mother_id       TEXT,
    father_id       TEXT,
    offspring_ids   TEXT NOT NULL DEFAULT '[]',
    gestation_id    TEXT,
    version         BIGINT NOT NULL DEFAULT 0,
    updated_at      TEXT NOT NULL,
    deleted_at      TEXT
);
CREATE INDEX animals_farm_updated ON animals(farm_id, updated_at);

CREATE TABLE animal_groups (
    id          TEXT PRIMARY KEY,
    farm_id     TEXT NOT NULL,
    name        TEXT NOT NULL,
    description TEXT NOT NULL,
    animal_ids  TEXT NOT NULL DEFAULT '[]',
    version     BIGINT NOT NULL DEFAULT 0,
    updated_at  TEXT NOT NULL,
    deleted_at  TEXT
);
CREATE INDEX groups_farm_updated ON animal_groups(farm_id, updated_at);

CREATE TABLE animal_events (
    id          TEXT PRIMARY KEY,
    farm_id     TEXT NOT NULL,
    animal_id   TEXT NOT NULL,
    type        TEXT NOT NULL,
    date        TEXT NOT NULL,
    time        TEXT NOT NULL,
    notes       TEXT NOT NULL,
    weight_kg   DOUBLE PRECISION,
    group_id    TEXT,
    version     BIGINT NOT NULL DEFAULT 0,
    updated_at  TEXT NOT NULL,
    deleted_at  TEXT
);
CREATE INDEX events_farm_updated ON animal_events(farm_id, updated_at);
CREATE INDEX events_animal ON animal_events(animal_id);

CREATE TABLE gestations (
    id                  TEXT PRIMARY KEY,
    farm_id             TEXT NOT NULL,
    animal_id           TEXT NOT NULL,
    start_date          TEXT NOT NULL,
    expected_birth_date TEXT NOT NULL,
    notes               TEXT NOT NULL,
    father_id           TEXT,
    version             BIGINT NOT NULL DEFAULT 0,
    updated_at          TEXT NOT NULL,
    deleted_at          TEXT
);
CREATE INDEX gestations_farm_updated ON gestations(farm_id, updated_at);

CREATE TABLE producao_entries (
    id              TEXT PRIMARY KEY,
    farm_id         TEXT NOT NULL,
    product_type    TEXT NOT NULL,
    quantity        DOUBLE PRECISION NOT NULL,
    unit            TEXT NOT NULL,
    date            TEXT NOT NULL,
    notes           TEXT NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,
    updated_at      TEXT NOT NULL,
    deleted_at      TEXT
);
CREATE INDEX producao_farm_updated ON producao_entries(farm_id, updated_at);

CREATE TABLE stock_items (
    id                  TEXT PRIMARY KEY,
    farm_id             TEXT NOT NULL,
    name                TEXT NOT NULL,
    category            TEXT NOT NULL,
    quantity            INTEGER NOT NULL,
    unit                TEXT NOT NULL,
    expiry_date         TEXT,
    low_stock_threshold INTEGER,
    version             BIGINT NOT NULL DEFAULT 0,
    updated_at          TEXT NOT NULL,
    deleted_at          TEXT
);
CREATE INDEX stock_farm_updated ON stock_items(farm_id, updated_at);
