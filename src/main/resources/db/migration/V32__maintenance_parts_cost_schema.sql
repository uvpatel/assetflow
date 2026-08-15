CREATE TABLE maintenance_part_items (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    maintenance_id UUID NOT NULL,
    part_name VARCHAR(128) NOT NULL,
    part_number VARCHAR(64),
    quantity_used INT NOT NULL,
    unit_cost NUMERIC(15, 2) NOT NULL,
    total_cost NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_part_items_org_maint ON maintenance_part_items(organization_id, maintenance_id);
