ALTER TABLE saved_search_presets
ADD COLUMN IF NOT EXISTS is_default BOOLEAN DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_saved_search_presets_default ON saved_search_presets(organization_id, user_id, target_entity, is_default);
