ALTER TABLE departments
ADD COLUMN IF NOT EXISTS parent_department_id UUID REFERENCES departments(id);

CREATE INDEX IF NOT EXISTS idx_department_parent ON departments(organization_id, parent_department_id);

