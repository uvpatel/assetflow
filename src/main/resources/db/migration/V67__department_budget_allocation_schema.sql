CREATE TABLE IF NOT EXISTS department_budgets (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    department_id UUID NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    fiscal_year INT NOT NULL,
    allocated_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    committed_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    spent_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    notes VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_dept_budget_org_dept_year UNIQUE(organization_id, department_id, fiscal_year)
);

CREATE INDEX IF NOT EXISTS idx_dept_budget_org_dept_year ON department_budgets(organization_id, department_id, fiscal_year);
CREATE INDEX IF NOT EXISTS idx_dept_budget_org_year ON department_budgets(organization_id, fiscal_year);
