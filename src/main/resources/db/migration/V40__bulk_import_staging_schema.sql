CREATE TABLE bulk_import_batches (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    filename VARCHAR(255) NOT NULL,
    total_records INT NOT NULL,
    processed_records INT NOT NULL DEFAULT 0,
    failed_records INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_import_batch_org ON bulk_import_batches(organization_id, status);
