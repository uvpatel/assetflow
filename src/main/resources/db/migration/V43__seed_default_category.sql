INSERT INTO asset_categories (id, organization_id, name, code, description, created_at, updated_at, version)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Hardware & IT Equipment', 'HW_IT', 'Laptops, Monitors, and Servers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
ON CONFLICT (id) DO NOTHING;
