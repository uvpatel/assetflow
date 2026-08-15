CREATE TABLE vendor_contracts (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    contract_number VARCHAR(64) NOT NULL,
    contract_type VARCHAR(64) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    contract_value NUMERIC(15, 2) NOT NULL,
    is_auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_contracts_org_vendor ON vendor_contracts(organization_id, vendor_id);
CREATE UNIQUE INDEX uq_contract_number ON vendor_contracts(organization_id, contract_number);
