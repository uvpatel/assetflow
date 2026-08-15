CREATE TABLE vendor_performance_evaluations (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    evaluator_user_id UUID NOT NULL,
    delivery_score INT NOT NULL,
    quality_score INT NOT NULL,
    communication_score INT NOT NULL,
    overall_rating NUMERIC(3, 2) NOT NULL,
    evaluation_notes TEXT,
    evaluated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_vendor_eval_org_vendor ON vendor_performance_evaluations(organization_id, vendor_id);
