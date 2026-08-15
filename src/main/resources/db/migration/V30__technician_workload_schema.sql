CREATE TABLE technician_workloads (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    technician_employee_id UUID NOT NULL,
    active_task_count INT NOT NULL DEFAULT 0,
    max_task_capacity INT NOT NULL DEFAULT 5,
    specialization VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX uq_technician_org_emp ON technician_workloads(organization_id, technician_employee_id);
