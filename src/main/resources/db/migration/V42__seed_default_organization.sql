INSERT INTO organizations (id, name, code, status, created_at, updated_at, version)
VALUES ('00000000-0000-0000-0000-000000000001', 'Default Organization', 'DEFAULT_ORG', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
ON CONFLICT (id) DO NOTHING;
