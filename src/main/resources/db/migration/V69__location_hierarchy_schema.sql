-- V69: Multi-Tier Physical Location Hierarchy (Campus, Building, Floor, Room, Storage Area)

CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    parent_location_id UUID REFERENCES locations(id) ON DELETE RESTRICT,
    address VARCHAR(255),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_location_org_code UNIQUE (organization_id, code)
);

CREATE INDEX IF NOT EXISTS idx_locations_org_id ON locations(organization_id);
CREATE INDEX IF NOT EXISTS idx_locations_parent_id ON locations(parent_location_id);
CREATE INDEX IF NOT EXISTS idx_locations_type ON locations(organization_id, type);
