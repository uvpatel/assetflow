CREATE TABLE feature_flags (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    flag_key VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX uq_feature_flag_org_key ON feature_flags(organization_id, flag_key);
