ALTER TABLE currency_exchange_rates
ADD COLUMN IF NOT EXISTS rate_source VARCHAR(50) DEFAULT 'MANUAL',
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_curr_exchange_pair ON currency_exchange_rates(organization_id, from_currency, to_currency, is_active);
