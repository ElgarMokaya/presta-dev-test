CREATE TABLE IF NOT EXISTS reconciliation_file (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_date DATE NOT NULL,
    filename VARCHAR(200) NOT NULL,
    source VARCHAR(20) NOT NULL, -- CSV or JSON
    ingested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
