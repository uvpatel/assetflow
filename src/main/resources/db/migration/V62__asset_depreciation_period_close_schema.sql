CREATE TABLE IF NOT EXISTS asset_depreciation_period_closing_runs (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    period_year INT NOT NULL,
    period_month INT NOT NULL,
    processed_schedules_count INT NOT NULL,
    total_depreciation_amount NUMERIC(19,4) NOT NULL,
    executed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    CONSTRAINT uq_depreciation_period UNIQUE(organization_id, period_year, period_month)
);
