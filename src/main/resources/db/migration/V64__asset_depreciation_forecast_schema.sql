CREATE TABLE IF NOT EXISTS asset_depreciation_forecast_projections (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    fiscal_year INT NOT NULL,
    projected_depreciation NUMERIC(19,4) NOT NULL,
    projected_ending_book_value NUMERIC(19,4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_depreciation_forecast UNIQUE(organization_id, fiscal_year)
);
