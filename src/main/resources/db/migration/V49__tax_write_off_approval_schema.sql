ALTER TABLE asset_tax_writeoffs
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'APPROVED',
ADD COLUMN IF NOT EXISTS approved_by UUID,
ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_tax_writeoff_status ON asset_tax_writeoffs(organization_id, status);

