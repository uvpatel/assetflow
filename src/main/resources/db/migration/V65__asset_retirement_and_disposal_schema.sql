CREATE TABLE IF NOT EXISTS asset_disposal_records (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    disposal_type VARCHAR(32) NOT NULL,
    disposal_date TIMESTAMPTZ NOT NULL,
    proceeds_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    disposal_reason VARCHAR(1000) NOT NULL,
    authorized_by_user_id UUID,
    certificate_reference VARCHAR(128),
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_asset_disposal_org_asset ON asset_disposal_records(organization_id, asset_id);
CREATE INDEX IF NOT EXISTS idx_asset_disposal_org_status ON asset_disposal_records(organization_id, status);
