CREATE TABLE saved_search_presets (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    target_entity VARCHAR(64) NOT NULL,
    filter_json TEXT NOT NULL,
    is_shared BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_preset_org_user ON saved_search_presets(organization_id, user_id);
CREATE INDEX idx_preset_org_target ON saved_search_presets(organization_id, target_entity);
