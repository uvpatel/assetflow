ALTER TABLE consumable_batches
ADD COLUMN IF NOT EXISTS is_expired BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_consumable_batches_expiry ON consumable_batches(organization_id, expiry_date, current_quantity);
