CREATE TABLE IF NOT EXISTS transfer_requests (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    employee_id UUID NOT NULL REFERENCES employees(id),
    from_department_id UUID REFERENCES departments(id),
    to_department_id UUID NOT NULL REFERENCES departments(id),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    rejection_reason TEXT,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    reviewed_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_transfer_requests_emp ON transfer_requests(employee_id);
CREATE INDEX idx_transfer_requests_org ON transfer_requests(organization_id);
