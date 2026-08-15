CREATE TABLE asset_depreciation_schedules (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    depreciation_method VARCHAR(32) NOT NULL,
    initial_cost NUMERIC(15, 2) NOT NULL,
    salvage_value NUMERIC(15, 2) NOT NULL,
    useful_life_months INT NOT NULL,
    current_book_value NUMERIC(15, 2) NOT NULL,
    accumulated_depreciation NUMERIC(15, 2) NOT NULL,
    last_calculated_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_depreciation_org_asset ON asset_depreciation_schedules(organization_id, asset_id);
