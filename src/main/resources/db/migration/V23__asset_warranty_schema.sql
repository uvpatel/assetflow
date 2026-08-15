CREATE TABLE asset_warranties (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    provider_name VARCHAR(128) NOT NULL,
    policy_number VARCHAR(64) NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    coverage_details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_warranty_org_asset ON asset_warranties(organization_id, asset_id);
CREATE INDEX idx_warranty_end_date ON asset_warranties(organization_id, end_date);
