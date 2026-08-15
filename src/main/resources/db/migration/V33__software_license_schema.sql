CREATE TABLE software_licenses (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    software_name VARCHAR(128) NOT NULL,
    license_key VARCHAR(128),
    total_seats INT NOT NULL,
    allocated_seats INT NOT NULL DEFAULT 0,
    expiration_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_license_org_name ON software_licenses(organization_id, software_name);
