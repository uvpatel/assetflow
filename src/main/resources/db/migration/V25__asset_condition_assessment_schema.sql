CREATE TABLE asset_condition_assessments (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    assessor_employee_id UUID NOT NULL,
    condition_score INT NOT NULL,
    assessment_notes TEXT,
    assessed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_condition_org_asset ON asset_condition_assessments(organization_id, asset_id);
