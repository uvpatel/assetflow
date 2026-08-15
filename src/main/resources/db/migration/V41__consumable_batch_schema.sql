CREATE TABLE IF NOT EXISTS consumable_batches (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    batch_number VARCHAR(128) NOT NULL,
    initial_quantity INT NOT NULL DEFAULT 0,
    current_quantity INT NOT NULL DEFAULT 0,
    expiry_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_consumable_batch_org_asset ON consumable_batches(organization_id, asset_id);
