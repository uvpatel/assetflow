ALTER TABLE vendor_contracts
ADD COLUMN IF NOT EXISTS notice_period_days INT DEFAULT 30,
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_vendor_contract_exp ON vendor_contracts(organization_id, end_date, status);
