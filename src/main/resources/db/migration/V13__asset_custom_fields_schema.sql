CREATE TABLE IF NOT EXISTS category_field_definitions (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    category_id UUID NOT NULL REFERENCES asset_categories(id),
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(255) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    options TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_cat_field_name UNIQUE (category_id, field_name)
);

CREATE INDEX idx_category_field_defs_org_cat ON category_field_definitions(organization_id, category_id);

CREATE TABLE IF NOT EXISTS asset_custom_field_values (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    asset_id UUID NOT NULL REFERENCES assets(id),
    field_definition_id UUID NOT NULL REFERENCES category_field_definitions(id),
    field_value TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_asset_field_value UNIQUE (asset_id, field_definition_id)
);

CREATE INDEX idx_asset_custom_field_values_org_asset ON asset_custom_field_values(organization_id, asset_id);
