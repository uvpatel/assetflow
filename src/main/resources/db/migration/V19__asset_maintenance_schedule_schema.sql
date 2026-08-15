CREATE TABLE asset_maintenance_schedules (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    title VARCHAR(128) NOT NULL,
    frequency_days INT NOT NULL,
    next_due_date TIMESTAMP WITH TIME ZONE NOT NULL,
    last_performed_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_maint_sched_org_asset ON asset_maintenance_schedules(organization_id, asset_id);
CREATE INDEX idx_maint_sched_due_date ON asset_maintenance_schedules(organization_id, next_due_date, status);
