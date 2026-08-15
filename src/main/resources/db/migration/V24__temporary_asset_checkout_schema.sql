CREATE TABLE temporary_asset_checkouts (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    borrower_employee_id UUID NOT NULL,
    checkout_time TIMESTAMP WITH TIME ZONE NOT NULL,
    expected_return_time TIMESTAMP WITH TIME ZONE NOT NULL,
    actual_return_time TIMESTAMP WITH TIME ZONE,
    checkout_condition_notes TEXT,
    return_condition_notes TEXT,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_checkout_org_asset ON temporary_asset_checkouts(organization_id, asset_id);
CREATE INDEX idx_checkout_borrower ON temporary_asset_checkouts(organization_id, borrower_employee_id);
CREATE UNIQUE INDEX uq_active_temp_checkout ON temporary_asset_checkouts(asset_id) WHERE actual_return_time IS NULL;
