CREATE TABLE data_retention_policies (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    target_module VARCHAR(64) NOT NULL,
    retention_days INT NOT NULL,
    action VARCHAR(32) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_retention_org_module ON data_retention_policies(organization_id, target_module);
