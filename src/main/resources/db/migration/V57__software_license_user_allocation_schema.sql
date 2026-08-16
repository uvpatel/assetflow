CREATE TABLE IF NOT EXISTS software_license_allocations (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    software_license_id UUID NOT NULL REFERENCES software_licenses(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES employees(id),
    allocated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uk_license_employee UNIQUE(software_license_id, employee_id)
);

CREATE INDEX IF NOT EXISTS idx_license_alloc_org ON software_license_allocations(organization_id, software_license_id);
