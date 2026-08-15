CREATE TABLE asset_bundle_items (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    parent_asset_id UUID NOT NULL,
    child_asset_id UUID NOT NULL,
    relationship_type VARCHAR(32) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_bundle_parent ON asset_bundle_items(organization_id, parent_asset_id);
CREATE INDEX idx_bundle_child ON asset_bundle_items(organization_id, child_asset_id);
