ALTER TABLE data_retention_policies
ADD COLUMN IF NOT EXISTS last_executed_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS records_processed INT DEFAULT 0;

CREATE TABLE IF NOT EXISTS data_retention_execution_logs (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    policy_id UUID NOT NULL REFERENCES data_retention_policies(id) ON DELETE CASCADE,
    target_module VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    cutoff_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    records_affected INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    executed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_retention_execution_logs ON data_retention_execution_logs(organization_id, policy_id);
