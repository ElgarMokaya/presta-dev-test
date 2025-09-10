CREATE TABLE IF NOT EXISTS reconciliation_summary (
    business_date DATE PRIMARY KEY,
    matched_count INT NOT NULL DEFAULT 0,
    missing_internal INT NOT NULL DEFAULT 0,
    missing_external INT NOT NULL DEFAULT 0,
    amount_mismatch INT NOT NULL DEFAULT 0,
    net_difference DECIMAL(19,2) NOT NULL DEFAULT 0
);