CREATE TABLE IF NOT EXISTS purchase_requisitions (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    requisition_number VARCHAR(64) NOT NULL,
    requester_id UUID NOT NULL,
    department_id UUID,
    title VARCHAR(255) NOT NULL,
    business_justification VARCHAR(1000) NOT NULL,
    estimated_total_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    urgency VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING_APPROVAL',
    approver_id UUID,
    approval_notes VARCHAR(1000),
    approved_at TIMESTAMPTZ,
    converted_purchase_order_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS purchase_requisition_items (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    requisition_id UUID NOT NULL REFERENCES purchase_requisitions(id) ON DELETE CASCADE,
    item_description VARCHAR(255) NOT NULL,
    category_id UUID,
    estimated_unit_price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_purchase_req_org_status ON purchase_requisitions(organization_id, status);
CREATE INDEX IF NOT EXISTS idx_purchase_req_org_req ON purchase_requisitions(organization_id, requester_id);
CREATE INDEX IF NOT EXISTS idx_purchase_req_items_req ON purchase_requisition_items(requisition_id);
