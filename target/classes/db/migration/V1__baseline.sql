-- Baseline migration for AssetFlow schema initialization
CREATE TABLE IF NOT EXISTS system_baseline (
    id VARCHAR(64) PRIMARY KEY,
    initialized_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_baseline (id) VALUES ('v0.1.0-baseline') ON CONFLICT DO NOTHING;
