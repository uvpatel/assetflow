CREATE TABLE api_keys (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    key_prefix VARCHAR(16) NOT NULL,
    key_hash VARCHAR(128) NOT NULL,
    scopes VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_api_key_org_prefix ON api_keys(organization_id, key_prefix);
CREATE UNIQUE INDEX uq_api_key_hash ON api_keys(key_hash);
