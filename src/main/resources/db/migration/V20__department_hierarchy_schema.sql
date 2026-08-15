ALTER TABLE departments
ADD COLUMN parent_department_id UUID REFERENCES departments(id);

CREATE INDEX idx_department_parent ON departments(organization_id, parent_department_id);
