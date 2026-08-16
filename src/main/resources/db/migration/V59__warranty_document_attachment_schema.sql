CREATE TABLE IF NOT EXISTS asset_warranty_documents (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    warranty_id UUID NOT NULL REFERENCES asset_warranties(id) ON DELETE CASCADE,
    document_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    storage_url VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_warranty_docs_warr_id ON asset_warranty_documents(warranty_id);
