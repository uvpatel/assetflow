CREATE TABLE currency_exchange_rates (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    exchange_rate NUMERIC(15, 6) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX uq_exchange_rate ON currency_exchange_rates(organization_id, from_currency, to_currency, effective_date);
