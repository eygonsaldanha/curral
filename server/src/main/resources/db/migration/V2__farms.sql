CREATE TABLE farms (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    owner_user_id   TEXT NOT NULL,
    created_at      TEXT NOT NULL,
    updated_at      TEXT NOT NULL
);
CREATE INDEX farms_owner_user ON farms(owner_user_id);

CREATE TABLE user_farms (
    user_id         TEXT NOT NULL,
    farm_id         TEXT NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    role            TEXT NOT NULL,
    created_at      TEXT NOT NULL,
    PRIMARY KEY (user_id, farm_id)
);
CREATE INDEX user_farms_user ON user_farms(user_id);
CREATE INDEX user_farms_farm ON user_farms(farm_id);
