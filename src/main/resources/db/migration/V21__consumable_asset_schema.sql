CREATE TABLE consumable_assets (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    sku VARCHAR(64) NOT NULL,
    category_id UUID NOT NULL,
    quantity_on_hand INT NOT NULL DEFAULT 0,
    minimum_threshold INT NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(32) NOT NULL,
    unit_cost NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_consumable_org_sku ON consumable_assets(organization_id, sku);
CREATE INDEX idx_consumable_org_threshold ON consumable_assets(organization_id, quantity_on_hand, minimum_threshold);
