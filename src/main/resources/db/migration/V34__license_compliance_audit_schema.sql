CREATE TABLE license_compliance_audits (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    license_id UUID NOT NULL,
    total_seats INT NOT NULL,
    allocated_seats INT NOT NULL,
    compliance_status VARCHAR(32) NOT NULL,
    audit_notes TEXT,
    audited_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_compliance_org_license ON license_compliance_audits(organization_id, license_id);
