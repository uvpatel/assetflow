CREATE TABLE IF NOT EXISTS custody_transfers (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    sender_employee_id UUID NOT NULL REFERENCES employees(id),
    recipient_employee_id UUID NOT NULL REFERENCES employees(id),
    status VARCHAR(50) NOT NULL,
    digital_signature TEXT,
    signed_at TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS custody_transfer_items (
    id UUID PRIMARY KEY,
    custody_transfer_id UUID NOT NULL REFERENCES custody_transfers(id) ON DELETE CASCADE,
    asset_id UUID NOT NULL REFERENCES assets(id),
    item_condition VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_custody_transfers_org ON custody_transfers(organization_id, recipient_employee_id);
CREATE INDEX IF NOT EXISTS idx_custody_transfer_items ON custody_transfer_items(custody_transfer_id);
