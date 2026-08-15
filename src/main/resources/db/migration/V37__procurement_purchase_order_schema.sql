CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    po_number VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_po_org_vendor ON purchase_orders(organization_id, vendor_id);
CREATE UNIQUE INDEX uq_po_number ON purchase_orders(organization_id, po_number);
