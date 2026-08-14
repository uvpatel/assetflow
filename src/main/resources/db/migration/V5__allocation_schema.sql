CREATE TABLE IF NOT EXISTS asset_allocations (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    asset_id UUID NOT NULL REFERENCES assets(id),
    allocated_to_employee_id UUID REFERENCES employees(id),
    allocated_to_department_id UUID REFERENCES departments(id),
    allocated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expected_return_at TIMESTAMP WITH TIME ZONE,
    returned_at TIMESTAMP WITH TIME ZONE,
    return_condition_state VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

-- Partial unique index enforcing a single active allocation per asset (Double Allocation Prevention invariant)
CREATE UNIQUE INDEX uq_active_asset_allocation 
ON asset_allocations(asset_id) 
WHERE returned_at IS NULL;
