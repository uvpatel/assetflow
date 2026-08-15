CREATE TABLE maintenance_sla_policies (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    priority VARCHAR(32) NOT NULL,
    response_deadline_hours INT NOT NULL,
    resolution_deadline_hours INT NOT NULL,
    escalation_email VARCHAR(128),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX uq_sla_org_priority ON maintenance_sla_policies(organization_id, priority);
