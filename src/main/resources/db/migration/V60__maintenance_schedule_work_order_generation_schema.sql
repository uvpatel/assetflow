ALTER TABLE asset_maintenance_schedules
ADD COLUMN IF NOT EXISTS auto_generate_work_order BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS priority VARCHAR(50) DEFAULT 'MEDIUM';

CREATE INDEX IF NOT EXISTS idx_maint_sched_due ON asset_maintenance_schedules(organization_id, next_due_date, status);
