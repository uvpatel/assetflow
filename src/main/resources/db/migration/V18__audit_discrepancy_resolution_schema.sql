CREATE TABLE audit_discrepancy_resolutions (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    audit_cycle_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    discrepancy_type VARCHAR(64) NOT NULL,
    resolution_action VARCHAR(64) NOT NULL,
    resolved_by UUID NOT NULL,
    notes TEXT,
    resolved_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_discrepancy_org_cycle ON audit_discrepancy_resolutions(organization_id, audit_cycle_id);
