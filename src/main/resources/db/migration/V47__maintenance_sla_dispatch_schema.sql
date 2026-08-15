ALTER TABLE maintenance_requests
ADD COLUMN IF NOT EXISTS target_resolution_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS sla_status VARCHAR(50) DEFAULT 'IN_SLA';

CREATE INDEX IF NOT EXISTS idx_maintenance_requests_sla ON maintenance_requests(organization_id, sla_status);
