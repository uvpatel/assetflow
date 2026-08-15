CREATE TABLE asset_tax_writeoffs (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    fiscal_year INT NOT NULL,
    writeoff_amount NUMERIC(15, 2) NOT NULL,
    remaining_tax_basis NUMERIC(15, 2) NOT NULL,
    writeoff_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_tax_writeoff_org_asset ON asset_tax_writeoffs(organization_id, asset_id);
CREATE UNIQUE INDEX uq_tax_writeoff_asset_year ON asset_tax_writeoffs(organization_id, asset_id, fiscal_year);
