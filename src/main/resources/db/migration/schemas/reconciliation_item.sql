CREATE TABLE IF NOT EXISTS reconciliation_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_date DATE NOT NULL,
    transaction_id VARCHAR(50) NOT NULL,
    internal_amount DECIMAL(19,2),
    external_amount DECIMAL(19,2),
    status VARCHAR(30) NOT NULL, -- MATCHED, MISSING_INTERNAL, etc.
    file_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recon_item_file FOREIGN KEY (file_id) REFERENCES reconciliation_file(id)
);